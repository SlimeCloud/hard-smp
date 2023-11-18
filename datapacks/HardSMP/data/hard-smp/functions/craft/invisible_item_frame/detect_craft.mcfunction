advancement revoke @s only hard-smp:invisible_item_frame
recipe take @a hard-smp:invisible_item_frame

execute unless entity @s[tag=invisible_item_frame] run scoreboard players reset @s invisible_item_frame
scoreboard players add @s invisible_item_frame 1
tag @s add invisible_item_frame

schedule function hard-smp:craft/invisible_item_frame/craft 2t