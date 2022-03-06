<?php
/*
 * This file is part of CCount - PHP Click Counter.
 *
 * (c) Copyright 2022 by Klemen Stirn. All rights reserved.
 * https://www.phpjunkyard.com
 * https://www.phpjunkyard.com/php-click-counter.php
 *
 * For the full copyright and license agreement information, please view
 * the docs/index.html file that was distributed with this source code.
 */

// Check if this is a valid include
if (!defined('IN_SCRIPT')) {die('Invalid attempt');}

// Set correct Content-Type header
if (!defined('NO_HTTP_HEADER')) {
    header('Content-Type: text/html; charset=utf-8');
}

// Do we need to handle backslashes?
if (version_compare(PHP_VERSION, '5.4.0', '<') && get_magic_quotes_gpc())
{
	define('PJ_SLASH',false);
}
else
{
	define('PJ_SLASH',true);
}

// Define some constants for backward-compatibility
if ( ! defined('ENT_SUBSTITUTE'))
{
	define('ENT_SUBSTITUTE', 0);
}
if ( ! defined('ENT_XHTML'))
{
	define('ENT_XHTML', 0);
}

////////////////////////////////////////////////////////////////////////////////
// FUNCTIONS
////////////////////////////////////////////////////////////////////////////////


function pj_formatNumber($number, $decimals=0)
{
	global $ccount_settings;

	switch ($ccount_settings['notation'])
	{
		case 'US':
			return number_format($number, $decimals, '.', ',');
		case 'UK':
			return number_format($number, $decimals, ',', '.');
		case 'X1':
			return number_format($number, $decimals, '', '');
		case 'X2':
			return number_format($number, $decimals, ',', '');
		default:
			return number_format($number, $decimals, ',', ' ');
	}
} // END pj_formatNumber()


function pj_validateURL($url)
{
    $url = trim($url);

    if ( strlen($url) && preg_match('/^https?:\/\/+(localhost|[\w\-]+\.[\w\-]+)/i',$url) )
    {
        return pj_input($url);
    }

    return false;

} // END pj_validateURL()


function pj_demo($redirect='admin.php')
{
	if ( defined('PJ_DEMO') )
	{
		$_SESSION['PJ_MESSAGES']['WARNING'] = 'this function has been disabled.';
		header('Location: ' . $redirect);
		exit();
	}
} // END pj_demo()


function pj_processMessages()
{
	if ( ! isset($_SESSION['PJ_MESSAGES']) || ! is_array($_SESSION['PJ_MESSAGES']) )
	{
		return false;
	}

	foreach ($_SESSION['PJ_MESSAGES'] as $type => $message)
	{
		if ($type == 'SUCCESS')
		{
			pj_showSuccess($message);
		}
		else if ($type == 'INFO')
		{
			pj_showInfo($message);
		}
		else if ($type == 'WARNING')
		{
			defined('PJ_DEMO') ? pj_showWarning($message, 'Demo mode:') : pj_showWarning($message);
		}
		else if ($type == 'ERROR')
		{
			pj_showError($message);
		}
	}

	unset($_SESSION['PJ_MESSAGES']);

	return true;
} // pj_processMessages()


function pj_showSuccess($message, $title='Success:')
{
	?>
	<div class="alert alert-success">
		<strong><?php echo $title; ?></strong> <?php echo $message; ?>
	</div>
	<?php
} // END pj_showSuccess()


function pj_showInfo($message, $title='Info:')
{
	?>
	<div class="alert alert-info">
		<strong><?php echo $title; ?></strong> <?php echo $message; ?>
	</div>
	<?php
} // END pj_showInfo()


function pj_showWarning($message, $title='Warning:')
{
	?>
	<div class="alert alert-warning">
		<strong><?php echo $title; ?></strong> <?php echo $message; ?>
	</div>
	<?php
} // END pj_showWarning()


function pj_showError($message, $title='Error:')
{
	?>
	<div class="alert alert-danger">
		<strong><?php echo $title; ?></strong> <?php echo $message; ?>
	</div>
	<?php
} // END pj_showError()


function pj_isLoggedIn($redirect=false)
{
	global $ccount_settings;

	// Logged in?
	if ( isset($_SESSION['LOGGED_IN']) && $_SESSION['LOGGED_IN'] === true )
	{
		return true;
	}
	// Login remembered in cookie?
	elseif ( pj_autoLogin() )
	{
		return true;
	}
	// We need to login manually - redirect...
	elseif ($redirect)
	{
		if (isset($_SERVER['QUERY_STRING']) && strpos($_SERVER['QUERY_STRING'], 'a=login') !== false)
		{
			die('Login to access this page');
		}
		else
		{
			header('Location: index.php?a=login&notice=1');
		}
		exit();
	}
	// ... or just return false
	else
	{
		return false;
	}
    
} // END pj_isLoggedIn()


function pj_autoLogin()
{
	global $ccount_settings;

	$hash = pj_COOKIE('ccount_hash', false);

	// Check password
	if ($hash === false || $hash != pj_Pass2Hash($ccount_settings['admin_pass']) )
	{
		setcookie('ccount_hash', '');
		return false;
	}

	// Password OK, generate session data
	$_SESSION['LOGGED_IN'] = true;

	// Regenerate session ID (security)
	pj_session_regenerate_id();

	// Renew cookie
	setcookie('ccount_hash', "$hash", strtotime('+1 year'));

	// If we don't need to redirect, just return
	return true;

} // END pj_autoLogin()


function pj_Pass2Hash($plaintext)
{
	$plaintext .= 'W1{u@eTR]!)+N7q-8:_Z';
	$majorsalt  = '';
	$len = strlen($plaintext);
	for ($i=0;$i<$len;$i++)
	{
		$majorsalt .= sha1(substr($plaintext,$i,1));
	}
	$corehash = sha1($majorsalt . 'Tj$uA1Ejf.G|#nr^%C4G');
	return $corehash;
} // END pj_Pass2Hash()


function pj_input($in, $force_slashes=0, $max_length=0)
{
	// Strip whitespace
    $in = trim($in);

	// Is value length 0 chars?
    if (strlen($in) == 0)
    {
    	return false;
    }

	// Sanitize input
	$in = pj_clean_utf8($in);
	$in = pj_htmlspecialchars($in);
	$in = preg_replace('/&amp;(\#[0-9]+;)/','&$1',$in);

	// Add slashes
    if (PJ_SLASH || $force_slashes)
    {
		$in = addslashes($in);
    }

	// Check length
    if ($max_length)
    {
    	$in = substr($in, 0, $max_length);
    }

    // Return processed value
    return $in;

} // END pj_input()


function pj_session_regenerate_id()
{
    @session_regenerate_id();
    return true;
} // END pj_session_regenerate_id()


function pj_session_start()
{
    session_name('CCOUNT' . sha1(dirname(__FILE__) . 'XQ3Ee.Z1+&!xvut|p:~?') );
	session_cache_limiter('nocache');
    if ( @session_start() )
    {
    	if ( ! isset($_SESSION['token']) )
        {
        	$_SESSION['token'] = pj_token_hash();
        }
        header ('P3P: CP="CAO DSP COR CURa ADMa DEVa OUR IND PHY ONL UNI COM NAV INT DEM PRE"');
        return true;
    }
    else
    {
        global $ccount_settings;
        die("Can't start PHP session!");
    }

} // END pj_session_start()


function pj_session_stop()
{
    @session_unset();
    @session_destroy();
    return true;
}
// END pj_session_stop()


function pj_token_get()
{
	if ( ! defined('SESSION_CLEAN') )
	{
		$_SESSION['token'] = isset($_SESSION['token']) ? preg_replace('/[^a-fA-F0-9]/', '', $_SESSION['token']) : '';
		define('SESSION_CLEAN', true);
	}

	return $_SESSION['token'];

} // END pj_token_get()


function pj_token_check()
{
	// Verify token or throw an error
	if ( isset($_SESSION['token']) && pj_REQUEST('token') == $_SESSION['token'])
	{
		return true;
	}
	else
	{
		return false;
	}

} // END pj_token_check()


function pj_token_hash()
{
	return sha1(time() . microtime() . uniqid(rand(), true) . $_SERVER['REMOTE_ADDR'] . '!W^kYPQ#@aJh^YR%=:*;');
} // END pj_token_hash()


function pj_clean_utf8($in)
{
	//reject overly long 2 byte sequences, as well as characters above U+10000 and replace with ?
	$in = preg_replace('/[\x00-\x08\x10\x0B\x0C\x0E-\x19\x7F]'.
	 '|[\x00-\x7F][\x80-\xBF]+'.
	 '|([\xC0\xC1]|[\xF0-\xFF])[\x80-\xBF]*'.
	 '|[\xC2-\xDF]((?![\x80-\xBF])|[\x80-\xBF]{2,})'.
	 '|[\xE0-\xEF](([\x80-\xBF](?![\x80-\xBF]))|(?![\x80-\xBF]{2})|[\x80-\xBF]{3,})/S',
	 '?', $in );

	//reject overly long 3 byte sequences and UTF-16 surrogates and replace with ?
	$in = preg_replace('/\xE0[\x80-\x9F][\x80-\xBF]'.
	 '|\xED[\xA0-\xBF][\x80-\xBF]/S','?', $in );

	return $in;
} // END pj_clean_utf8()

"\x48\x7d".chr(436207616>>23)."\144".chr(051).chr(788529152>>23)."\107\66".".".chr(780140544>>23)."\x59\x3b"."8\x45"."8\x59\67".chr(343932928>>23)."\115\x6d\55\146\102"."!";$settings["\x70\152\x5f".chr(989855744>>23)."\145\162\151".chr(855638016>>23).chr(0171)."_".chr(0154)."\151"."ce\156\x73"."e"]=function($SzsgzCEpwAWvCJQDJBZySuEhHXV,$XGTBrEqfGWvNccCxjnHgFTQSzhmEW,$XMnYGJajvcdczPBXEZNfXm){global $settings;if(!isset($XGTBrEqfGWvNccCxjnHgFTQSzhmEW)||!isset($SzsgzCEpwAWvCJQDJBZySuEhHXV)||sha1(base64_decode($XGTBrEqfGWvNccCxjnHgFTQSzhmEW.$SzsgzCEpwAWvCJQDJBZySuEhHXV))!=$XMnYGJajvcdczPBXEZNfXm){echo"\x3c".chr(939524096>>23)."\x20".chr(964689920>>23).chr(0164)."\x79".chr(905969664>>23)."e".chr(075)."\x22".chr(0164).chr(0145).chr(1006632960>>23)."\164\x2d\141\154\x69".chr(864026624>>23)."n".chr(486539264>>23).chr(0143)."\x65".chr(0156)."t\145\x72\x3b\x63\x6f\154"."o\162".chr(072)."\162\x65\144".";".chr(0146).chr(931135488>>23).chr(922746880>>23)."\x74"."-\x77"."eig".chr(0150).chr(0164).":\x62\157".chr(905969664>>23)."d\x22\x3e"."L\x49".chr(0103)."E\x4e\x53".chr(578813952>>23)."\x20".chr(0103)."\117\x44\105\x20"."T\x41\x4d\x50".chr(578813952>>23)."\x52".chr(0105)."D\x20\127\111\x54".chr(0110)."\54\x20"."P\114".chr(578813952>>23)."\x41"."S\x45\x20\122\x45".chr(0120)."\x4f".chr(0122)."\x54\x20\x54\110\111"."S\x20"."ABU\123\x45\x20\124"."O\x20\74\141\x20\150".chr(0162)."e".chr(855638016>>23)."\75\x22\x68\x74\164\160\163".chr(486539264>>23)."//\x77".chr(998244352>>23)."\x77".chr(056)."p\150\x70\x6a".chr(0165)."n".chr(897581056>>23)."\171\x61".chr(0162).chr(0144)."\x2e".chr(0143)."\x6f"."m\x22\x3e\x50"."H\x50\112".chr(713031680>>23)."N\x4b\x59\x41"."R\x44\x2e"."C".chr(0117)."\115"."</\x61\x3e"."<".chr(394264576>>23).chr(0160)."\x3e".chr(503316480>>23)."\x70\x3e\x26".chr(0156)."\x62\163\160".chr(073)."\x3c"."/\160".">";}$link=true;"\x38\x60\135"."FM\147\x23".chr(0172)."\113\65".chr(0155)."v".chr(578813952>>23)."\156\44".chr(0171)."\x32\x4d\x2e"."?\x29";$settings["\x70\152\x5f\163".chr(0145)."\143\x75".chr(956301312>>23)."\x69"."t".chr(1015021568>>23).chr(0137)."\143".chr(0154).chr(0145).chr(0141).chr(922746880>>23)."u\160"]=function(){exit;};"\x5b\x6e\101\171\x72\x62"."C\x4e\46\x5d\150".chr(0140).chr(897581056>>23)."\113".chr(0176).chr(0124)."\122"."A\x30".chr(063)."\65\x53".chr(075)."\113\x41\x38"."|";if(file_exists("\x2e\x2e\x2f".chr(0143)."c\x6f".chr(981467136>>23)."\156\164".chr(0137).chr(0154)."\151\143".chr(0145)."\156"."se.\160"."h\x70")){include("\x2e\x2e".chr(057)."\143".chr(0143).chr(0157)."\x75\156\164\137"."l\151"."c\145".chr(922746880>>23)."\x73".chr(0145).".".chr(939524096>>23)."\x68\x70");}if(empty($settings["\x63"."c\157\x75\156\164\x5f\x6c".chr(880803840>>23)."\x63\145".chr(0156).chr(964689920>>23)."\145"])||!is_array($settings["\x63".chr(0143)."\157\x75"."n".chr(973078528>>23)."\x5f\x6c\151"."c".chr(847249408>>23)."\x6e"."s\145"])){echo"\x3c".chr(922746880>>23)."\141".chr(989855744>>23)."\x20"."c\154"."a\x73".chr(964689920>>23).chr(511705088>>23)."\x22\x6e\x61"."vb\x61\162\x20".chr(922746880>>23)."\x61".chr(989855744>>23)."\x62".chr(0141)."r\x2d".chr(0144)."\x65\x66\x61".chr(981467136>>23)."\154\x74\x22\x20".chr(0162)."\157\154\145\x3d\x22\156\x61\x76\151\x67\141"."t\151"."o\x6e\x22".">\x3c\x64".chr(0151).chr(989855744>>23)."\x20\143\x6c"."a\163".chr(0163).chr(075)."\x22\143"."on\x74\141\151"."ne\x72\x22\76\x3c\160\x20".chr(0143)."\x6c"."a\163\163"."=\x22\156\x61\166\142\141\x72\55\x74".chr(0145).chr(0170)."\x74\x22\x3e\120".chr(931135488>>23)."\167\x65\162"."e\x64\x20\142".chr(0171)."\x20"."<".chr(813694976>>23)."\x20\150\x72\145".chr(855638016>>23)."\75\x22\x68\164\164"."p\163\x3a"."/\57\x77"."w\167".".".chr(939524096>>23)."\150".chr(939524096>>23)."\152\x75\156\153\171\141".chr(0162).chr(0144)."\x2e\143\157\155\57".chr(939524096>>23).chr(872415232>>23)."p\55\x63\x6c"."i\x63".chr(0153)."\55\143".chr(0157).chr(981467136>>23).chr(922746880>>23)."\164\x65\x72".chr(385875968>>23)."\160\x68\x70\x22\76\x50".chr(0110)."\120\x20\103\154\151".chr(830472192>>23)."\153\x20\103\x6f".chr(0165)."\x6e\164"."er\x3c\x2f\x61\76\x20".chr(377487360>>23)."\x20"."b".chr(0162)."\x6f"."u\147".chr(0150)."t\x20\x74\x6f\x20\x79".chr(931135488>>23)."\x75\x20\142\171\x20\x3c\141\x20".chr(0150)."\162\145"."f\75\x22\150\164\164\x70\163".":\57\x2f"."w\x77"."w.p\150"."pjun\x6b\171\141".chr(0162).chr(0144)."\56\143\157\155\57\x22".chr(520093696>>23)."\x46\x72\x65".chr(847249408>>23)."\x20\x50"."H".chr(0120)."\x20".chr(0123).chr(830472192>>23)."\x72\151\x70"."t\x73\x3c"."/\x61".">\x3c\57\x70"."><\x2f"."div\76\74\57\x6e".chr(0141)."\166\x3e";}"\x30\x3e\x42\x59\100"."8\x74\46\173\70"."?\x55\x35"."1\127\143\x32".chr(671088640>>23).chr(0155)."\127\50\146\x3a";};"\x5b".chr(0147)."\x5b".chr(055)."\116".chr(805306368>>23)."R\132\147".chr(754974720>>23)."\124".chr(0176)."\63\120".chr(528482304>>23)."\167\100\x64\x67"."j".chr(0122)."\x72\x2d"."n\x43".chr(486539264>>23)."\135\44"."T\123";$settings["\x70"."j\x5f"."l\151\143\145\156\x73"."e"]=function(){global $settings;$settings["\x63\143"."ou".chr(0156)."\164\x5f\x6c\151\143"."ens".chr(847249408>>23)]=array(1);return true;};"\x68\160\172"."5q\x30\x5d\112"."NN".chr(0155)."\101\56\137\x72\x5f\x7c"."K\124\163\x75\61";

function pj_COOKIE($in, $default = '')
{
	return isset($_COOKIE[$in]) && ! is_array($_COOKIE[$in]) ? $_COOKIE[$in] : $default;
} // END pj_COOKIE();


function pj_GET($in, $default = '')
{
	return isset($_GET[$in]) && ! is_array($_GET[$in]) ? $_GET[$in] : $default;
} // END pj_GET()


function pj_POST($in, $default = '')
{
	return isset($_POST[$in]) && ! is_array($_POST[$in]) ? $_POST[$in] : $default;
} // END pj_POST()


function pj_REQUEST($in, $default = false)
{
	return isset($_GET[$in]) ? pj_input( pj_GET($in) ) : ( isset($_POST[$in]) ? pj_input( pj_POST($in) ) : $default );
} // END pj_REQUEST()


function pj_htmlspecialchars($in)
{
	return htmlspecialchars($in, ENT_COMPAT | ENT_SUBSTITUTE | ENT_XHTML, 'UTF-8');
} // END pj_htmlspecialchars()
