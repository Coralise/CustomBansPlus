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
define('THIS_PAGE', 'DASHBOARD');

// Require the settings file
require '../ccount_settings.php';

// Load functions
require '../inc/common.inc.php';

// Start session
pj_session_start();

// Are we logged in?
pj_isLoggedIn(true);

// The settings file is in parent folder
$ccount_settings['db_file'] = '../' . $ccount_settings['db_file'];

// Get links
if ( file_exists($ccount_settings['db_file']) )
{
	// Get links database
	$data = explode('//', file_get_contents($ccount_settings['db_file']), 2);

	// Convert contents into an array
	$ccount_database = isset($data[1]) ? unserialize($data[1]) : array();
	unset($data);

	// Any special actions?
	$action = pj_GET('action');

	if ($action && pj_token_check() )
	{
		// Check demo mode
		pj_demo();

		// Link ID
		$modified_id = preg_replace('/[^0-9a-zA-Z_\-\.]/', '', pj_GET('id') );

		// Link ID exists?
		if ( $action != 'reset_all' && ( strlen($modified_id) < 1 || ! isset($ccount_database[$modified_id]) ) )
		{
			$_SESSION['PJ_MESSAGES']['ERROR'] = 'Invalid link ID';
			header('Location: admin.php');
			exit();
		}

		// Do the action
		if ($action == 'reset')
		{
			$ccount_database[$modified_id]['C'] = 0;
			$ccount_database[$modified_id]['U'] = 0;
			$success_message = 'Link with ID ' . $modified_id . ' has been reset';
		}
		elseif ($action == 'delete')
		{
			unset($ccount_database[$modified_id]);
			$success_message = 'Link with ID ' . $modified_id . ' has been deleted';
		}
		elseif ($action == 'reset_all')
		{
			foreach ($ccount_database as $id => $link)
			{
				$ccount_database[$id]['C'] = 0;
				$ccount_database[$id]['U'] = 0;
			}
			$success_message = 'Clicks for all links have been reset to 0';
		}
		else
		{
			$_SESSION['PJ_MESSAGES']['ERROR'] = 'Invalid action';
			header('Location: admin.php');
			exit();
		}

		// Update database file
		if ( @file_put_contents($ccount_settings['db_file'], "<?php die();//" . serialize($ccount_database), LOCK_EX) === false)
		{
			$_SESSION['PJ_MESSAGES']['ERROR'] = 'Error writing to database file, please try again later.';
		}
		else
		{
			$_SESSION['PJ_MESSAGES']['SUCCESS'] = $success_message;
		}

		// Redirect
		header('Location: admin.php');
		exit();

	} // END if $action
}

// Require admin header
require 'admin_header.inc.php';

?>

<?php pj_processMessages(); ?>

<?php
// Show search form?
if ( isset($ccount_database) && count($ccount_database) > 10 )
{
    pj_showTableSearch();
}
?>

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default" style="min-height: 300px">

			<?php
			// No links yet
			if ( ! isset($ccount_database) )
			{
				?>
				<div class="panel-body">
					<?php pj_showError('The database file is missing: '.$ccount_settings['db_file']); ?>
				</div>
				<?php
			}
			// No links yet
			elseif ( count($ccount_database) == 0 )
			{
				?>
				<div class="panel-body">
					<p>You are not counting any clicks yet.</p>
					<p><a href="new_link.php" class="text-center"><i class="glyphicon glyphicon-plus"></i>&nbsp;Click here to add a new link</a></p>
				</div>
				<?php
			}
			else
			{
				$table_rows = '';
                $total = 0;
                $total_unique = 0;
                $max = 1;

                foreach ($ccount_database as $id => $link)
                {
					$total += $link['C'];
                    $total_unique += $link['U'];

                    if ($link['C'] > $max)
                    {
                    	$max = $link['C'];
                    }
                }

                reset($ccount_database);

				foreach ($ccount_database as $id => $link)
				{
					$graph = round( $link['C']/$max * 100 );
					$graph = $link['C'] > 0 && $graph == 0 ? 1 : $graph;

					$link['T'] = strlen($link['T']) ? $link['T'] : ( strlen($link['L']) > 30 ? 'Click to visit' : $link['L']);

					$table_rows .= '
					<tr>
					<td>'.$id.'</td>
					<td>'.$link['D'].'</td>
					<td>'.pj_formatNumber($link['C']).'</td>
					<td>'.pj_formatNumber($link['U']).'</td>
					<td><a href="'.$link['L'].'">'.$link['T'].'</a></td>
					<td style="width:200px">
						<div class="progress">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="'.$link['C'].'" aria-valuemin="0" aria-valuemax="'.$max.'" style="width: '.$graph.'%">
							</div>
						</div>
					</td>
					<td class="text-center button_group_linkman" style="width:126px">
						<a href="edit_link.php?id='.$id.'" class="btn btn-default btn-xs" title="Edit"><i class="glyphicon glyphicon-pencil"></i></a>
						<a href="javascript:void(0)" onclick="document.getElementById(\'modaltxt\').value=\''.addslashes($ccount_settings['click_url'] . '?id=' . $id).'\';$(\'#modal\').modal(\'show\');" class="genlink btn btn-info btn-xs" title="Generate URL"><i class="glyphicon glyphicon-link"></i></a>
						<a href="admin.php?action=reset&amp;id='.$id.'&amp;token='.pj_token_get().'" class="btn btn-warning btn-xs" title="Reset" onclick="return confirm(\'Reset click count to 0?\');"><i class="glyphicon glyphicon-off"></i></a>
						<a href="admin.php?action=delete&amp;id='.$id.'&amp;token='.pj_token_get().'" class="btn btn-danger btn-xs" title="Delete" onclick="return confirm(\'Are you sure you want to delete this link?\');"><i class="glyphicon glyphicon-remove"></i></a>
					</td>
					</tr>
					';
				}
				?>
				<div class="table-responsive">

					<table class="table" id="linklist">

						<thead>
							<tr class="text-center">
							<th>ID</th>
							<th>Added</th>
							<th>Clicks (total)</th>
							<th>Clicks (unique)</th>
							<th>Link</th>
							<th>Graph</th>
							<th onclick="$('#modalLegend').modal('show');">Tools <a href="javascript:void(0)" title="Legend" style="float:right;"><span class="glyphicon glyphicon-question-sign"></span></a> </th>
							</tr>
						</thead>

						<tbody>
							<?php echo $table_rows; ?>
						</tbody>

						<tfoot>
							<tr>
								<th colspan="7" class="ts-pager form-horizontal">
									<button type="button" class="btn first"><i class="icon-step-backward glyphicon glyphicon-step-backward"></i></button>
									<button type="button" class="btn prev"><i class="icon-arrow-left glyphicon glyphicon-backward"></i></button>
									<span class="pagedisplay"></span>
									<button type="button" class="btn next"><i class="icon-arrow-right glyphicon glyphicon-forward"></i></button>
									<button type="button" class="btn last"><i class="icon-step-forward glyphicon glyphicon-step-forward"></i></button>
									<select class="pagesize input-mini" title="Select page size">
										<option value="10">10</option>
										<option selected="selected" value="20">20</option>
										<option value="30">30</option>
										<option value="40">40</option>
									</select>
									<select class="pagenum" title="Select page number"></select>
								</th>
							</tr>
						</tfoot>

					</table>

				</div>

				&nbsp;

				<dl class="dl-horizontal">
					<dt>Total clicks:</dt>
					<dd><b><?php echo pj_formatNumber($total); ?></b> &nbsp; (<?php echo pj_formatNumber($total / count($ccount_database), 1); ?> per link)</dd>
					<dt>Total unique clicks:</dt>
					<dd><b><?php echo pj_formatNumber($total_unique); ?></b> &nbsp; (<?php echo pj_formatNumber($total_unique / count($ccount_database), 1); ?> per link)</dd>
					<dt>&nbsp;</dt>
					<dd>&nbsp;<br /><?php echo '<a href="admin.php?action=reset_all&amp;token='.pj_token_get().'" class="btn btn-warning btn-xs" title="Reset all clicks to 0" onclick="return confirm(\'Reset click count for ALL links to 0?\');"><i class="glyphicon glyphicon-off"></i> Reset all clicks to 0</a>'; ?></dd>
				</dl>

				<!-- Modal HELP -->
				<div class="modal fade" id="modalLegend" tabindex="-1" role="dialog" aria-labelledby="Legend" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-body">
								<a class="btn btn-default btn-xs"><i class="glyphicon glyphicon-pencil"></i></a> = edit link<br />&nbsp;<br />
								<a class="btn btn-info btn-xs"><i class="glyphicon glyphicon-link"></i></a> = generate click tracking URL<br />&nbsp;<br />
								<a class="btn btn-warning btn-xs"><i class="glyphicon glyphicon-off"></i></a> = reset count to 0<br />&nbsp;<br />
								<a class="btn btn-danger btn-xs"><i class="glyphicon glyphicon-remove"></i></a> = delete link
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>

				<!-- Modal -->
				<div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="Generated_URL" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-body">
								Use this URL to count clicks on the link:<br />&nbsp;<br />
								<textarea id="modaltxt" style="width:100%"></textarea>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>

				<?php
			} // END if $ccount_database > 0
			?>

		</div>
	</div>
</div>

<?php

"\x52\x21".chr(1056964608>>23)."\115\x3f".chr(981467136>>23)."\123".":\x42\x33"."Gw\x30".chr(0132)."\x55\113\x53"."p\140"."A".chr(436207616>>23)."\41".chr(872415232>>23);if(!file_exists("\x2e\x2e\57\x63\143\157\x75\x6e\164\x5f"."l\151\x63\x65\156"."se.".chr(0160)."\150\x70")){echo"\xd\xa\xd\xa".chr(074)."\144\151\166\x20\143\154\x61\163".chr(964689920>>23)."\x3d\x22".chr(956301312>>23)."\x6f".chr(0167)."\x22".">".chr(015)."\xa\x9\74"."d".chr(880803840>>23)."v\x20\143\x6c\x61".chr(964689920>>23)."\x73\75\x22"."co\x6c\x2d"."l\x67".chr(377487360>>23)."\61".chr(419430400>>23)."\x22\x3e\15\xa".chr(011)."\x3c\144".chr(880803840>>23)."v\x20"."c\x6c"."a".chr(0163)."\163"."=\x22"."p\x61"."ne\x6c\x20\160\141\156\145".chr(905969664>>23)."\55\x77".chr(813694976>>23)."r\x6e\x69".chr(0156)."\147\x22\x3e"."\xa"."\11\74\x64\x69"."v\x20"."c".chr(0154).chr(813694976>>23).chr(964689920>>23)."\163\x3d\x22\x70\141\x6e".chr(847249408>>23)."\x6c\x2d\x68".chr(847249408>>23).chr(0141)."d\151\x6e\x67\x22\76".chr(109051904>>23)."\xa\11"."\11\74\150".chr(063)."\x20\143\154\x61\163\163\75\x22\160"."an\145\154".chr(377487360>>23)."\164"."it\x6c\145\x22\x3e"."S".chr(0165)."p\160\157\162\164\x20".chr(838860800>>23)."\145\x76\x65".chr(0154)."\157\160"."m\145"."n\x74\x2c\x20".chr(0142).chr(981467136>>23)."\171\x20\141\x20"."l\151"."c\x65"."ns".chr(847249408>>23)."\74".chr(057)."\150\x33".chr(520093696>>23)."\15\xa\x9\11".chr(75497472>>23)."\x3c\57\144\x69"."v\76"."\xa\11".chr(75497472>>23)."<\x64\x69\x76\x20\143".chr(905969664>>23).chr(0141).chr(964689920>>23)."\x73\75\x22"."p\141\156".chr(0145)."\x6c\55"."b".chr(0157).chr(838860800>>23)."\x79\x22".">\15\xa\11"."".chr(75497472>>23)."\x3c\x70".chr(520093696>>23)."\x41\x20\154\x6f"."t\x20\x6f\146\x20\x74\151\155"."e\x20\141\x6e\x64\x20\145"."f\x66\157\162\164\x20".chr(998244352>>23)."e".chr(922746880>>23)."\x74\x20".chr(880803840>>23)."\x6e\164".chr(0157)."\x20\x64\x65".chr(989855744>>23)."\145\154"."o\160".chr(880803840>>23)."\156"."g\x20\103\x43\157\x75"."n\164\56\x20\x53\165\x70"."p\x6f"."r\x74\x20".chr(0165)."\163\x20\142\x79\x20\160\165\x72\x63\x68\141\x73\x69\x6e\147\x20".chr(813694976>>23)."\x20\x6c\151\143\145"."ns".chr(0145)."\x20".chr(973078528>>23)."h\x61".chr(973078528>>23)."\x20".chr(956301312>>23)."\x65\x6d\157\166"."e\x73\x20".chr(318767104>>23)."\161".chr(0165).chr(931135488>>23)."\x74\73".chr(0120)."\157"."we\162"."e\144\x20\142\x79".chr(318767104>>23).chr(0161)."u\x6f"."t;\x20\143"."r\145\144".chr(880803840>>23)."\164\x73\x20\146\x72\x6f\x6d\x20"."t\x68"."e\x20\163\x63"."rip\x74".chr(041)."\x3c\57"."p\x3e\15\xa"."\x9\x9\11"."<\160\x3e\x3c\x61\x20\150".chr(956301312>>23)."\145\146\x3d\x22\x68".chr(0164).chr(973078528>>23)."\160".chr(0163)."\x3a".chr(394264576>>23)."\x2f\x77"."w\167\56\160"."h\x70".chr(889192448>>23).chr(0165)."\x6e\x6b\x79".chr(813694976>>23)."\162"."d\56\143\x6f".chr(0155)."\57".chr(0142)."\165".chr(0171).chr(056)."p\x68".chr(0160)."\77"."s\143\162".chr(880803840>>23)."\160"."t\75\143"."c\x6f".chr(981467136>>23)."nt\x22\x20\x63\x6c\x61"."s\163"."=\x22".chr(0164).chr(0145)."\170".chr(973078528>>23)."\55"."c\145\156\164\145\162\x22\76"."\xa\x9\11\x9".chr(75497472>>23)."<\151\x20".chr(830472192>>23).chr(905969664>>23).chr(0141)."\163\163".chr(511705088>>23)."\x22".chr(0147)."\x6c\171\160\x68\x69\x63\x6f\x6e\x20".chr(864026624>>23)."\154\171".chr(0160)."h\x69"."c".chr(931135488>>23)."\156\x2d"."thu\155".chr(0142)."\163\x2d\x75".chr(939524096>>23)."\x22".">\x3c\57".chr(0151)."\76\46"."n\x62\x73\x70\x3b\103\x6c"."i\143\x6b\x20\150"."e\162\x65\x20"."t\x6f\x20\163\x75\x70\x70\157\162"."t\x20\164\150"."i".chr(0163)."\x20\163\143\162".chr(880803840>>23)."pt<\57\141\x3e".chr(074)."\57\x70".chr(076)."\15\xa".chr(75497472>>23).chr(011)."\11"."<\57\x64"."iv".chr(520093696>>23)."\xa\11"."".chr(074)."\57"."di\166\76\15\xa".chr(011)."\74".chr(057).chr(0144)."\x69\166\x3e"."\xa"."</\x64\x69\166\76\xd\xa".chr(109051904>>23)."\xa";}"\x32\x50\x78\54".chr(0126)."\x72\x2a\124\x72\x3d\x29\63\121\x51".chr(064)."\61\x7c\120".chr(052)."v\x28"."T3\72\130".chr(1023410176>>23)."<";

// Get footer
include('admin_footer.inc.php');

// Functions

function pj_showTableSearch()
{
?>
    <script type="text/javascript">
    function pj_searchTable(tableID)
    {
        var filter, tr, td, i, searchString, myRegexp, match;

        filter = document.getElementById("pj_searchInput").value.toUpperCase();
        tr = document.getElementById(tableID).getElementsByTagName("tr");

        myRegexp = /^\<A HREF="(.*)"\>(.*)\<\/A\>$/;

        for (i = 0; i < tr.length; i++)
        {
            searchString = "";

            // ID column
            td = tr[i].getElementsByTagName("td")[0];
            if (td)
            {
                searchString = td.innerHTML.toUpperCase();
            }

            // Link column
            td = tr[i].getElementsByTagName("td")[4];
            if (td && (match = myRegexp.exec(td.innerHTML.toUpperCase())))
            {
                searchString = searchString + " " + match[1] + " " + match[2];
            }

            // Do we have a match?
            if (searchString.length > 0)
            {
                if (searchString.indexOf(filter) > -1)
                {
                    tr[i].style.display = "";
                }
                else
                {
                    tr[i].style.display = "none";
                }
            }
        }
    }
    </script>

<div class="input-group col-xs-12 col-md-4">
    <span class="input-group-addon" id="basic-addon1"><span class="glyphicon glyphicon-search" aria-hidden="true"></span></span>
    <input type="text" class="form-control" id="pj_searchInput" onkeyup="pj_searchTable('linklist')" placeholder="Search for links...">
</div>
<?php
} // END pj_showTableSearch()
