package edu.cvtc.ttm.tabletopmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import edu.cvtc.ttm.tabletopmanager.DatabaseContract.CharactersTable;
import edu.cvtc.ttm.tabletopmanager.DatabaseContract.InventoryTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(charactersTableCreateStatement());
        db.execSQL(inventoryTableCreateStatement());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void newCharacter(String characterName, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(CharactersTable.COLUMN_CHARACTER_NAME, characterName);

        db.insertOrThrow(CharactersTable.TABLE, null, values);
    }

    //Delete character and corresponding inventory from their tables
    public void deleteCharacter(String characterName, SQLiteDatabase db) {
        db = getWritableDatabase();
        String[] whereArgs = {characterName};

        db.delete(InventoryTable.TABLE, InventoryTable.COLUMN_CHARACTER_NAME + " = ?", whereArgs);
        db.delete(CharactersTable.TABLE, CharactersTable.COLUMN_CHARACTER_NAME + " = ?", whereArgs);
    }

    public void addNewItem(String charName, String itemName, String weight, String cost, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(InventoryTable.COLUMN_CHARACTER_NAME, charName);
        values.put(InventoryTable.COLUMN_ITEM_NAME, itemName);
        values.put(InventoryTable.COLUMN_ITEM_WEIGHT, weight);
        values.put(InventoryTable.COLUMN_ITEM_COST, cost);

        db.insertOrThrow(InventoryTable.TABLE, null, values);
    }

    public void deleteItem(String itemID, SQLiteDatabase db) {
        db = getWritableDatabase();
        String[] whereArgs = {itemID};

        db.delete(InventoryTable.TABLE, InventoryTable.COLUMN_INVENTORY_ID + " = ?", whereArgs);
    }

    public  ArrayList<String> getSelectedCharacter(SQLiteDatabase db) {
        String[] columns = {CharactersTable.COLUMN_CHARACTER_NAME};
        Cursor cursor = db.query(CharactersTable.TABLE, columns, null, null, null, null, null);
        ArrayList<String> characterNameList = new ArrayList<>();
        while (cursor.moveToNext()) {
            characterNameList.add(cursor.getString(cursor.getColumnIndex(CharactersTable.COLUMN_CHARACTER_NAME)));
        }

        cursor.close();
        return characterNameList;
    }
/*

    //TODO: This is for a piece of future work that is in limbo, it is commented out for security and clarity
    public  ArrayList<String> getSelectedItem(SQLiteDatabase db) {
        String[] columns = {InventoryTable.COLUMN_INVENTORY_ID, InventoryTable.COLUMN_ITEM_NAME};
        Cursor cursor = db.query(InventoryTable.TABLE, columns, null, null, null, null, null);
        ArrayList<String> itemNameList = new ArrayList<>();
        while (cursor.moveToNext()) {
            itemNameList.add(cursor.getString(cursor.getColumnIndex(InventoryTable.COLUMN_INVENTORY_ID)));
            itemNameList.add(cursor.getString(cursor.getColumnIndex(InventoryTable.COLUMN_ITEM_NAME)));
        }

        cursor.close();
        return itemNameList;
    }
*/

    public Cursor fetchCharacterData(SQLiteDatabase db) {
        String str = "SELECT rowid _id, * FROM characters";
        return db.rawQuery(str,null);
    }

    public Cursor fetchInventoryData(SQLiteDatabase db, String charName) {
        String str = "SELECT rowid _id, * FROM inventory WHERE " + InventoryTable.COLUMN_CHARACTER_NAME + " = " + "'" + charName + "'";
        return db.rawQuery(str,null);
    }


    public Cursor fetchWeightOfGear(SQLiteDatabase db, String charName) {
        String str = "SELECT SUM(ItemWeight) FROM inventory WHERE CharacterName = '" + charName + "'";
        return db.rawQuery(str,null);
    }

    public Cursor fetchMaxWeight(SQLiteDatabase db, String charName) {
        String str = "SELECT MaxWeight FROM characters WHERE CharacterName = '" + charName + "'";
        return db.rawQuery(str,null);
    }

    public void updateMaxWeight(SQLiteDatabase db, String charName, String newWeight) {
        String str = "UPDATE characters SET MaxWeight='" + newWeight + "' WHERE CharacterName = '" + charName + "'";
        db.execSQL(str);
    }

    //checks to see if a given name is already in the DB or not
    public boolean compareNewName(SQLiteDatabase db, String charName) {
        //String str = "SELECT COUNT(CharacterName) FROM characters WHERE CharacterName = '" + charName + "'";
        String str = "SELECT CharacterName FROM characters WHERE CharacterName = '" + charName + "'";
        Cursor compare = db.rawQuery(str, null);
        /*String boolString = compare.getString(1);
        if (Integer.parseInt(boolString) == 1) {
            return true;
        } else {
            return false;
        }*/

        if (compare.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor fetchNetWorth(SQLiteDatabase db, String charName) {
        String str = "SELECT Gold FROM characters WHERE CharacterName = '" + charName + "'";
        return db.rawQuery(str,null);
    }

    public void increaseNetWorth(SQLiteDatabase db, String charName, String gold) {
        String str = "UPDATE characters SET Gold=Gold + '" + gold + "' WHERE CharacterName = '" + charName + "'";
        db.execSQL(str);
    }

    public void decreaseNetWorth(SQLiteDatabase db, String charName, String gold) {
        String str = "UPDATE characters SET Gold=Gold - '" + gold + "' WHERE CharacterName = '" + charName + "'";
        db.execSQL(str);
    }

    public static String charactersTableCreateStatement() {
        return "CREATE TABLE " +
                CharactersTable.TABLE + " ( " +
                CharactersTable.COLUMN_CHARACTER_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                CharactersTable.COLUMN_CHARACTER_GOLD + " INTEGER DEFAULT 0, " +
                CharactersTable.COLUMN_CHARACTER_MAXWEIGHT + " INTEGER DEFAULT 99999, " +
                CharactersTable.COLUMN_CHARACTER_NAME + " TEXT);";
    }

    public static String inventoryTableCreateStatement() {
        return "CREATE TABLE " +
                InventoryTable.TABLE + " ( " +
                InventoryTable.COLUMN_INVENTORY_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                InventoryTable.COLUMN_CHARACTER_NAME + " TEXT, " +
                InventoryTable.COLUMN_ITEM_NAME + " TEXT, " +
                InventoryTable.COLUMN_ITEM_WEIGHT + " TEXT, " +
                InventoryTable.COLUMN_ITEM_COST + " TEXT);";
    }
}
