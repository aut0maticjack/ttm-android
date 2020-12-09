package edu.cvtc.ttm.tabletopmanager;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static final String DB_NAME = "ttm.com.cvtc.edu.tabletopmanager";
    public static final int DB_VERSION = 2;
    public static final String _ID = "id";

    public class CharactersTable implements BaseColumns {
        public static final String TABLE = "characters";
        public static final String COLUMN_CHARACTER_ID = "id";
        public static final String COLUMN_CHARACTER_NAME = "CharacterName";
        public static final String COLUMN_CHARACTER_GOLD = "Gold";
        public static final String COLUMN_CHARACTER_MAXWEIGHT = "MaxWeight";

    }

    public class InventoryTable implements BaseColumns {
        public static final String TABLE = "inventory";
        public static final String COLUMN_INVENTORY_ID = "id";
        public static final String COLUMN_CHARACTER_NAME = "CharacterName";
        public static final String COLUMN_ITEM_NAME = "ItemName";
        public static final String COLUMN_ITEM_WEIGHT = "ItemWeight";
        public static final String COLUMN_ITEM_COST = "ItemCost";

    }

}
