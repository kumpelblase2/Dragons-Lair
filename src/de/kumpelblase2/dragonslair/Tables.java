package de.kumpelblase2.dragonslair;

public enum Tables
{
	CHAPTERS("chapters", "CREATE TABLE `chapters` (" + 
			"`chapter_id` int(11) unsigned AUTO_INCREMENT," +
			"`chapter_name` varchar(50) NOT NULL," + 
			"`chapter_order_id` smallint(6) DEFAULT NULL," + 
			"PRIMARY KEY (`chapter_id`)" + 
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	DIALOGS("dialogs", "CREATE TABLE `dialogs` (" +
			"`dialog_id` int(10) unsigned AUTO_INCREMENT," +
			"`dialog_text` text," +
			"`next_agreement_id` int(10) unsigned DEFAULT NULL," +
			"`next_consider_agreement_id` int(10) unsigned DEFAULT NULL," +
			"`next_disagreement_id` int(11) DEFAULT NULL," +
			"`next_consider_disagreement_id` int(11) DEFAULT NULL," +
			"`next_consider_id` int(11) DEFAULT NULL," +
			"PRIMARY KEY (`dialog_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	OBJECTIVES("objectives", "CREATE TABLE `objectives` (" +
			"`objective_id` int(10) unsigned AUTO_INCREMENT," +
			"`objective_description` varchar(255) DEFAULT NULL, " +
			"PRIMARY KEY (`objective_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	EVENTS("events", "CREATE TABLE `events` (" +
			"`event_id` int(10) unsigned AUTO_INCREMENT," +
  			"`event_action_type` enum('npc_dialog','mob_spawn','block_change','level_change','objective_complete','npc_spawn','npc_despawn','item_remove','player_warp','dungeon_start','dungeon_end','dungeon_register','item_add','chapter_complete','npc_attack','npc_stop_attack','npc_walk') DEFAULT NULL," +
  			"`event_action_options` varchar(255) DEFAULT NULL," +
  			"PRIMARY KEY (`event_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;"),
	NPCS("npcs", "CREATE TABLE `npcs` (" +
			"`npc_id` int(10) unsigned AUTO_INCREMENT," +
			"`npc_name` varchar(16) NOT NULL," +
			"`npc_skin` text," +
			"`npc_location` varchar(255) NOT NULL," +
			"`npc_held_item` smallint(5) unsigned DEFAULT NULL," +
			"`npc_armor` varchar(255) DEFAULT NULL," +
			"`npc_spawned_from_beginning` tinyint(1) unsigned DEFAULT NULL," +
			"PRIMARY KEY (`npc_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	TRIGGERS("triggers", "CREATE TABLE `triggers` ( " +
			"`trigger_id` int(10) unsigned AUTO_INCREMENT, " +
			"`trigger_type` enum('block_place','npc_interact','movement','block_break','npc_touch','dialog_occur','block_interact','chapter_change','objective_change','npc_damage') DEFAULT NULL, " +
			"`trigger_type_options` varchar(255) DEFAULT NULL, " +
			"`trigger_action_event` varchar(255) DEFAULT NULL, " +
			"PRIMARY KEY (`trigger_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	DUNGEONS("dungeons", "CREATE TABLE `dungeons` (" +
			"`dungeon_id` int(10) unsigned AUTO_INCREMENT," +
			"`dungeon_name` varchar(255) NOT NULL," +
			"`dungeon_starting_objective` int(10) unsigned DEFAULT NULL," +
			"`dungeon_starting_chapter` int(10) unsigned DEFAULT NULL," +
			"`dungeon_starting_pos` varchar(255) DEFAULT NULL," +
			"`dungeon_safe_word` varchar(255) DEFAULT NULL," +
			"`dungeon_min_players` smallint(5) unsigned DEFAULT NULL," +
			"`dungeon_max_players` smallint(5) unsigned DEFAULT NULL," +
			"`dungeon_start_message` varchar(255) DEFAULT NULL," +
			"`dungeon_end_message` varchar(255) DEFAULT NULL," +
			"`dungeon_party_ready_message` varchar(255) DEFAULT NULL," +
			"PRIMARY KEY (`dungeon_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	PARTIES("parties", "CREATE TABLE `parties` (" +
			"`party_id` bigint(20) unsigned AUTO_INCREMENT," +
			" `party_members` varchar(255) DEFAULT NULL," +
			" `party_objective_id` int(10) unsigned DEFAULT NULL," +
			" `party_chapter_id` int(10) unsigned DEFAULT NULL," +
			" `party_dungeon_id` int(10) unsigned DEFAULT NULL," +
			" PRIMARY KEY (`party_id`)" +
			") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1"),
	PLAYER_SAVES("player_saves", "CREATE TABLE `player_saves` ( " +
			"`player_name` varchar(16) NOT NULL, " +
			"`player_armor` varchar(255) DEFAULT NULL, " +
			"`player_items` text, " +
			"`player_health` tinyint(2) DEFAULT NULL, " +
			"`player_hunger` tinyint(2) DEFAULT NULL, " +
			"`player_location` varchar(255) DEFAULT NULL, " +
			"`player_party_id` int(10) unsigned DEFAULT NULL, " +
			"PRIMARY KEY (`player_name`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=latin1"),
	LOG("log", "CREATE TABLE `log` (" +
			"`dungeon_name` varchar(255) NOT NULL," +
			"`party_id` int(11) NOT NULL," +
			"`log_type` enum('block_remove','block_place','data_change','block_change') DEFAULT NULL," +
			"`location` varchar(255) NOT NULL," +
			"`before_data` text," +
			"`after_data` text," +
			"PRIMARY KEY (`dungeon_name`,`party_id`,`location`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=latin1");
	
	private String table;
	private String create;
	private Tables(String name, String creatQuery)
	{
		this.table = name;
		this.create = creatQuery;
	}
	
	@Override
	public String toString()
	{
		return this.table;
	}
	
	public String getCreatingQuery()
	{
		return this.create;
	}
}
