USE cbp;
SELECT staff.player_ign, duration, reason, lift_by, date
FROM cbp.punishments pu
JOIN cbp.players staff ON pu.staff_id = staff.player_id
JOIN cbp.players target ON pu.player_id = target.player_id
WHERE active = 'True' AND type = '%s' AND target.player_ign = '%s';

SELECT duration
FROM cbp.punishments pu
JOIN cbp.players pl USING (player_id)
WHERE pl.player_ign = '%s' AND active = 'True';

UPDATE cbp.players
SET is_banned = 'True'
WHERE player_ign = '%s';

SELECT lift_by
FROM cbp.punishments pu
JOIN cbp.players pl USING (player_id)
WHERE pl.player_ign = '%s';

UPDATE cbp.players
SET is_banned = 'True'
WHERE player_ign = '%s';

UPDATE cbp.punishments pu
JOIN cbp.players pl USING (player_id)
SET active = 'False'
WHERE player_ign = '%s' AND type = '%s';

SELECT staff.player_ign AS staff_punisher, duration, reason, lift_by 
                    FROM cbp.punishments pu 
                    LEFT JOIN cbp.players staff ON staff_id = staff.player_id 
                    LEFT JOIN cbp.players target ON pu.player_id = target.player_id 
                    WHERE active = 'True' AND type = 'Ban' 
                    AND target.player_ign = 'Jablinski90';

SELECT lift_by FROM cbp.punishments pu 
JOIN cbp.players pl USING (player_id) WHERE pl.player_ign = 'Coralise';

SELECT target.player_ip, alts.player_ign, alts.is_banned 
FROM cbp.players target 
JOIN cbp.players alts USING (player_ip) 
WHERE target.player_ign = 'Coralise';

INSERT INTO `cbp`.`players` (`player_uuid`, `player_ign`, `player_ip`, `join_date`, `is_muted`, `is_banned`) VALUES ('423542323', 'Delinquent', '435.23.51.2', '2021-02-01', 'False', 'False');


SELECT player_ip
FROM cbp.players
WHERE player_ign = '%s';

SELECT ip
FROM cbp.banned_ips
WHERE ip = '%s';

INSERT INTO `cbp`.`banned_ips` (`ip`, `duration`, `reason`, `staff_id`, `date`, `lift_by`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');

SELECT pl.player_ign AS target, duration, reason, staff_id, staff.player_ign AS banner, lift_by
FROM cbp.players pl
JOIN cbp.banned_ips bi ON pl.player_ip = bi.ip
JOIN cbp.players staff ON bi.staff_id = staff.player_id;

SELECT target.player_ign AS target_ign, duration, reason, staff.player_ign AS banner, lift_by
FROM cbp.banned_ips bi
LEFT JOIN cbp.players target ON bi.ip = target.player_ip
LEFT JOIN cbp.players staff ON bi.staff_id = staff.player_id
WHERE target.player_ign = 'Coralise';

SELECT lift_by
FROM banned_ips
WHERE ip = '%s' AND active = 'True';

SELECT target.player_ign
    FROM cbp.banned_ips bi
    LEFT JOIN cbp.players target ON bi.ip = target.player_ip
    LEFT JOIN cbp.players staff ON bi.staff_id = staff.player_id
    WHERE bi.ip = '127.0.0.1';

SELECT type
FROM cbp.punishments pu
JOIN cbp.players pl USING (player_id)
WHERE pl.player_ign = '%s' AND active = 'True';

UPDATE cbp.banned_ips
SET duration = '%s',
    reason = '%s',
    staff_id = '%s',
    date = '%s',
    lift_by = '%s',
    active = 'True'
WHERE ip = '%s';

UPDATE cbp.banned_ips bi
JOIN cbp.players pl ON bi.ip = pl.player_ip
SET bi.active = '%s';

SELECT *
FROM cbp.banned_ips bi
JOIN cbp.players pl ON bi.ip = pl.player_ip
WHERE pl.player_ign = 'Coralise';

UPDATE cbp.punishments SET active = 'True' WHERE player_ign = '%s' AND  type = 'IP Ban' ORDER BY punishment_id DESC LIMIT 1;

SELECT * FROM punishments WHERE active = 'True' AND  type = 'IP Ban' AND  player_ign = '%s';

SELECT list.player_ign
FROM cbp.players target
JOIN cbp.banned_ips bi ON target.player_ip = bi.ip
JOIN cbp.players list ON bi.ip = list.player_ip
WHERE target.player_ign = 'Coralise';

INSERT INTO `cbp`.`punishments` (`player_ign`, `type`, `reason`, `staffer`, `date`) VALUES ('%s', 'Kick', '%s', '%s', '%s');

UPDATE `cbp`.`active_bans` 
SET `ban_type` = '%s', 
`banner_uuid` = '%s', 
`ban_reason` = 'sadas', 
`ban_date` = '%s', 
`ban_duration` = '%s', 
`unban_date` = '%s' 
WHERE player_uuid = '%s';

UPDATE `cbp`.`players` SET `is_banned` = '%s' WHERE (`player_id` = '2');

SELECT * FROM cbp.active_bans WHERE ban_type = 'Temp IP Ban IP' AND  banned_ip = '127.0.0.1' ORDER BY ban_id DESC;

UPDATE cbp.players SET is_banned = 'False' WHERE player_ip = '%s';

INSERT INTO cbp.players (`player_uuid`, `player_ign`, `player_ip`, `join_date`) 
VALUES ('%s', '%s', '%s', '%s');

UPDATE cbp.players
SET player_ip = '%s'
WHERE player_uuid = '%s';

SELECT unban_date
FROM cbp.active_bans ab 
JOIN cbp.players pl USING (player_uuid) 
WHERE pl.player_ign = 'MisterTester';

INSERT INTO cbp.active_bans (ban_type, player_uuid, banned_ip, banner_uuid, ban_reason, ban_date, ban_duration, unban_date)
SELECT '%s' AS 'ban_type', '%s' AS player_uuid, banned_ip, banner_uuid, ban_reason, ban_date, ban_duration, unban_date
FROM cbp.active_bans
WHERE banned_ip = '%s' AND ban_type LIKE '%%IP';

UPDATE pl
SET pl.ban_type = ip.ban_type,
    pl.banner_uuid = ip.banner_uuid,
    pl.ban_reason = ip.ban_reason,
    pl.ban_date = ip.ban_date,
    pl.ban_duration = ip.ban_duration,
    pl.unban_date = ip.unban_date
FROM cbp.active_bans pl
INNER JOIN cbp.active_bans ip USING (banned_ip)
WHERE pl.player_uuid = '%s';

INSERT INTO cbp.player_histories (player_uuid, punishment_type, staff_uuid, punishment_reason, punishment_date, punishment_duration, unpunish_date)
VALUES ('uuid', 'type', 'stafferUuid', 'reason', 'punishDate', 'duration', 'unpunishDate');

UPDATE cbp.player_histories 
SET status = '%s'
WHERE player_uuid = '%s' AND status = 'Active' AND punishment_type LIKE '%%%s%%';

UPDATE cbp.player_histories 
SET staff_updater_uuid = '%s'
WHERE player_uuid = '%s' AND status IN ('Unbanned', 'Unmuted') AND staff_updater_uuid IS NULL;

SELECT * 
FROM cbp.player_histories 
WHERE player_uuid = '%s' 
ORDER BY history_id DESC;

SELECT COUNT(history_id) AS size
FROM cbp.player_histories 
WHERE player_uuid = '%s' 
ORDER BY history_id DESC;