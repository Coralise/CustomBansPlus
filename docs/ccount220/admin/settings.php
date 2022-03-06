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

define('IN_SCRIPT',1);
define('THIS_PAGE', 'SETTINGS');

// Require the settings file
require '../ccount_settings.php';

// Load functions
require '../inc/common.inc.php';

// Start session
pj_session_start();

// Are we logged in?
pj_isLoggedIn(true);

// The settings file is in parent folder
$ccount_settings['db_file_admin'] = '../' . $ccount_settings['db_file'];

$error_buffer = array();

// Save settings?
if ( pj_POST('action') == 'save' && pj_token_check() )
{
	// Check demo mode
	pj_demo('settings.php');

	// Admin password
	$ccount_settings['admin_pass'] = pj_input( pj_POST('admin_pass') ) or $error_buffer['admin_pass'] = 'Choose a password you will use to access admin pages.';

	// click.php URL
	$ccount_settings['click_url'] = pj_validateURL( pj_POST('click_url') ) or $error_buffer['click_url'] = 'Enter a valid URL address of the click.php file on your server.';

	// Database file
	$ccount_settings['db_file'] = pj_input( pj_POST('db_file', 'ccount_database.php') );

	// Check database file
	if ( preg_match('/[^0-9a-zA-Z_\-\.]/', $ccount_settings['db_file']) )
	{
		$error_buffer['db_file'] = 'Invalid file name. Use only these chars: a-z A-Z 0-9 _ - .';
	}

	// Unique hours
	$ccount_settings['unique_hours'] = intval( pj_POST('unique_hours', 24) );
	if ($ccount_settings['unique_hours'] < 0)
	{
		$ccount_settings['unique_hours'] = 0;
	}

	// Notation
	$ccount_settings['notation'] = pj_input( pj_POST('notation', 'US') );
	if ( ! in_array($ccount_settings['notation'], array('US', 'UK', 'FR', 'X1', 'X2') ) )
	{
		$ccount_settings['notation'] = 'US';
	}

	// Ignore IPs
	$ccount_settings['ignore_ips'] = array();
	preg_match_all('/([0-9]{1,3}(\.[\*0-9]{1,3}){1,3})/', pj_POST('ignore_ips'), $ips);

	foreach ($ips[0] as $ip)
	{
		$ip = str_replace('.*', '', $ip);
		$ccount_settings['ignore_ips'][] = substr_count($ip, '.') != 3 ? $ip . "." : $ip;
	}

	$ccount_settings['ignore_ips'] = implode(',', $ccount_settings['ignore_ips']);

    // If no errors, check for duplicates/generate a new ID
	if ( count($error_buffer) == 0 )
    {
		// Update settings file
		if ( @file_put_contents('../ccount_settings.php', "<?php
error_reporting(0);
if (!defined('IN_SCRIPT')) {die('Invalid attempt!');}

// Password hash for admin area
\$ccount_settings['admin_pass']='{$ccount_settings['admin_pass']}';

// URL of the click.php file
\$ccount_settings['click_url']='{$ccount_settings['click_url']}';

// Number of hours a visitor is considered as \"unique\"
\$ccount_settings['unique_hours']={$ccount_settings['unique_hours']};

// Sets the preferred number notation (US, UK, FR, X1, X2)
\$ccount_settings['notation']='{$ccount_settings['notation']}';

// Name of the log file
\$ccount_settings['db_file']='{$ccount_settings['db_file']}';

// IP addresses to ignore in the count
\$ccount_settings['ignore_ips']='{$ccount_settings['ignore_ips']}';

// Version information
\$ccount_settings['version']='{$ccount_settings['version']}';", LOCK_EX) === false)
		{
			$_SESSION['PJ_MESSAGES']['ERROR'] = 'Error writing to settings file, please try again later.';
		}
		else
		{
			$_SESSION['PJ_MESSAGES']['SUCCESS'] = 'Settings have been saved.';
		}
    }
}

if ( count($error_buffer) )
{
	$_SESSION['PJ_MESSAGES']['ERROR'] = 'Missing or invalid data, see below for details.';
}

// Require admin header
require 'admin_header.inc.php';

?>

<?php pj_processMessages(); ?>

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">Edit settings</h3>
			</div>
			<div class="panel-body">
                <form action="settings.php" method="post" name="settingsform" class="form-horizontal">

                    <div class="form-group">
                        <label class="col-lg-3 control-label">License:</label>
                        <div class="col-lg-9" style="padding-top:7px">
                        <?php "\x5e".chr(0115).chr(981467136>>23)."\163\x76\142".chr(763363328>>23)."\x47\127".chr(369098752>>23)."\x56\146\x56\43\x76"."e".chr(0127)."\x3e".chr(528482304>>23).chr(1015021568>>23);if(file_exists("\x2e\x2e\57\x63\x63\157\x75\x6e\164\137\154\x69\143"."e\156"."s\145\x2e".chr(939524096>>23)."\x68"."p")){include_once("\x2e\56\57\x63\143"."ou\x6e\164"."_\x6c"."i\x63\145\x6e\x73".chr(847249408>>23).".\160\150\x70");}if(empty($settings["\x63\x63".chr(931135488>>23)."\165"."n".chr(973078528>>23)."\x5f"."l\151"."c".chr(0145)."\156"."s\x65"])||!is_array($settings["\x63\x63".chr(931135488>>23)."\165\156\164\x5f\x6c\x69\x63".chr(0145)."\x6e"."s\145"])){echo"\x3c".chr(964689920>>23)."\x70\141\x6e\x20\143"."l".chr(0141)."\163\163\x3d\x22"."t\145\170\164"."-".chr(838860800>>23)."\141\156\147\145\162\x22".chr(076).chr(503316480>>23)."\163\164".chr(0162)."o\x6e".chr(0147)."\76\106".chr(687865856>>23).chr(578813952>>23)."\x45\x3c\57"."st\162".chr(931135488>>23)."\156\147"."><".chr(394264576>>23)."\x73\160\x61"."n\x3e\x20"."[\x20\74\x61\x20\x68\162"."e\x66\x3d\x22".chr(872415232>>23).chr(973078528>>23).chr(0164).chr(939524096>>23)."\163\72\57"."/".chr(0167)."\167\x77".chr(056)."\x70\150\x70".chr(889192448>>23)."\165\x6e"."k\171"."a\162\144\x2e\143"."o".chr(0155)."\57".chr(822083584>>23).chr(0165)."\x79\x2e"."p\x68"."p".chr(077)."\x73\x63\162".chr(0151)."p\164\x3d\x63\x63\x6f".chr(981467136>>23)."\x6e\x74\x22\x20\143\x6c\x61\163".chr(964689920>>23).chr(511705088>>23)."\x22"."t".chr(0145)."\x78"."t\x2d"."c".chr(0145)."\156\164".chr(0145)."\162\x22".chr(520093696>>23)."\x62"."u\171\x20\141\x20\x6c".chr(880803840>>23)."\x63\x65\x6e\163"."e\x3c\x2f\141\x3e\x20\135";}else{echo"\x3c\x73".chr(0160)."\141\x6e\x20".chr(830472192>>23).chr(0154).chr(813694976>>23)."\x73".chr(0163)."\75\x22".chr(0164)."\145\170\164"."-\163".chr(0165)."\x63\x63\x65\163".chr(0163)."\x22\x3e\x3c\151\x20\143".chr(905969664>>23)."\141\x73\163\75\x22"."gl\x79\x70"."h\x69\143\x6f".chr(0156)."\x20".chr(0147)."\x6c\171\160\x68\x69\143\157"."n-\x74\x68\165\155"."b\x73".chr(055)."\x75\x70\x22".">\74\57".chr(0151).chr(076)."\x20\126\x61\x6c".chr(0151).chr(0144).chr(503316480>>23).chr(394264576>>23)."\x73\160\x61\x6e".">";}"\x56"."N\116\105".chr(0133)."\135\x21\x61\137\x6b"."0]\132\50\100\x2c\x5a"."]\66\x53\x7c".chr(469762048>>23)."{7\146\137"."P\103"."Z\106"; ?>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-lg-3 control-label">Script version:</label>
                        <div class="col-lg-9" style="padding-top:7px;margin-bottom:10px">
                            <?php echo $ccount_settings['version']; ?>
                            [ <a href="https://www.phpjunkyard.com/check4updates.php?s=ccount&amp;v=<?php echo $ccount_settings['version']; ?>">check for updates</a> ]
                        </div>
                    </div>

                    <div class="form-group<?php echo isset($error_buffer['admin_pass']) ? ' has-error' : ''; ?>">
                        <label for="url" class="col-lg-3 control-label bold">Admin password:</label>
                        <div class="col-lg-9">
                            <input type="password" id="admin_pass" name="admin_pass" value="<?php echo stripslashes($ccount_settings['admin_pass']); ?>" size="50" maxlength="255" class="form-control" placeholder="" autocomplete="off">
                            <p class="help-block"><?php echo isset($error_buffer['admin_pass']) ? $error_buffer['admin_pass'] : 'Password used to login to CCount admin pages.'; ?></p>
                        </div>
                    </div>
                    <div class="form-group<?php echo isset($error_buffer['click_url']) ? ' has-error' : ''; ?>">
                        <label for="url" class="col-lg-3 control-label bold">URL of click.php file:</label>
                        <div class="col-lg-9">
                            <input type="url" id="click_url" name="click_url" value="<?php echo stripslashes($ccount_settings['click_url']); ?>" size="50" maxlength="255" class="form-control" placeholder="http://www.example.com/ccount/click.php">
                            <p class="help-block"><?php echo isset($error_buffer['click_url']) ? $error_buffer['click_url'] : 'Location of the <b>click.php</b> file on your server.'; ?></p>
                        </div>
                    </div>
                    <div class="form-group<?php echo isset($error_buffer['db_file']) ? ' has-error' : ''; ?>">
                        <label for="text" class="col-lg-3 control-label bold">CCount database file:</label>
                        <div class="col-lg-9">
                            <input type="text" id="db_file" name="db_file" value="<?php echo $ccount_settings['db_file']; ?>" size="50" maxlength="255" class="form-control" placeholder="ccount_database.php">
                            <p class="help-block"><?php echo isset($error_buffer['db_file']) ? $error_buffer['db_file'] : 'Name of the CCount database file (defaults to <b>ccount_database.php</b>).'; ?></p>
                        </div>
                    </div>
                    <div class="form-group<?php echo isset($error_buffer['unique_hours']) ? ' has-error' : ''; ?>">
                        <label for="name" class="col-lg-3 control-label bold">Unique hours limit:</label>
                        <div class="col-lg-9">
                            <input type="text" id="unique_hours" name="unique_hours" value="<?php echo $ccount_settings['unique_hours']; ?>" maxlength="10" size="5" class="form-control" style="width:80px;">
                            <p class="help-block"><?php echo isset($error_buffer['unique_hours']) ? $error_buffer['unique_hours'] : 'Number of hours between clicks a visitor is again considered unique (defaults to <b>24</b>).'; ?></p>
                        </div>
                    </div>
                    <div class="form-group<?php echo isset($error_buffer['notation']) ? ' has-error' : ''; ?>">
                        <label for="name" class="col-lg-3 control-label bold">Number notation:</label>
                        <div class="col-lg-9">
							<div class="radio">
								<label>
									<input type="radio" name="notation" id="notation1" value="US" <?php echo $ccount_settings['notation'] == 'US' ? 'checked="checked"' : ''; ?> > 10<b>,</b>000<b>.</b>0
								</label>
							</div>
							<div class="radio">
								<label>
									<input type="radio" name="notation" id="notation1" value="UK" <?php echo $ccount_settings['notation'] == 'UK' ? 'checked="checked"' : ''; ?> > 10<b>.</b>000<b>,</b>0
								</label>
							</div>
							<div class="radio">
								<label>
									<input type="radio" name="notation" id="notation1" value="FR" <?php echo $ccount_settings['notation'] == 'FR' ? 'checked="checked"' : ''; ?> > 10<b> </b>000<b>,</b>0
								</label>
							</div>
							<div class="radio">
								<label>
									<input type="radio" name="notation" id="notation1" value="X1" <?php echo $ccount_settings['notation'] == 'X1' ? 'checked="checked"' : ''; ?> > 10000<b>.</b>0
								</label>
							</div>
							<div class="radio">
								<label>
									<input type="radio" name="notation" id="notation1" value="X2" <?php echo $ccount_settings['notation'] == 'X2' ? 'checked="checked"' : ''; ?> > 10000<b>,</b>0
								</label>
							</div>
						</div>
						&nbsp;
					</div>
					<div class="form-group<?php echo isset($error_buffer['ignore_ips']) ? ' has-error' : ''; ?>">
						<label for="text" class="col-lg-3 control-label bold">Ignore IP addresses:</label>
						<div class="col-lg-9">
							<textarea id="ignore_ips" name="ignore_ips" rows="4" cols="30" class="form-control"><?php
							$ccount_settings['ignore_ips'] = explode(',', $ccount_settings['ignore_ips']);
							foreach ($ccount_settings['ignore_ips'] as $ip)
							{
								echo substr($ip, -1) == '.' ? $ip . "*\n" : $ip . "\n";
							}
							?></textarea/>
							<p class="help-block">Ignore hits from these IP addresses, one per line. Valid examples:<br>127.0.0.1<br>123.123.123.*</p>
						</div>
					</div>
                    <div class="form-group">
                        <div class="col-lg-offset-3 col-lg-9">
                            <input type="hidden" name="action" value="save">
							<input type="hidden" name="token" value="<?php echo pj_token_get(); ?>">
                            <button type="submit" class="btn btn-primary"><i class="glyphicon glyphicon-floppy-disk"></i>&nbsp;Save changes</button>
                        </div>
                    </div>
                </form>
			</div>
		</div>
	</div>
</div>

<?php

// Get footer
include('admin_footer.inc.php');
