package edu.cvtc.ttm.tabletopmanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import edu.cvtc.ttm.tabletopmanager.DatabaseContract.InventoryTable;

import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    EditText itemName;
    EditText itemWeight;
    EditText itemCost;
    TextView title;
    TextView totalGold;
    TextView totalWeight;
    ListView inventoryDisplay;
    DatabaseHelper dbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();

        final String selectedCharName = intent.getExtras().getString("name");



        //final Integer passedCharID = intent.getExtras().getInt("id");
        //final String selectedCharID = passedCharID.toString();

        itemName = findViewById(R.id.itemNameEditText);
        itemWeight = findViewById(R.id.itemWeightEditText);
        itemCost = findViewById(R.id.itemCostEditText);
        totalGold = findViewById(R.id.goldTrackerAmtDisplay);
        totalWeight = findViewById(R.id.weightTrackerAmtDisplay);
        inventoryDisplay = findViewById(R.id.inventoryList);
        title = findViewById(R.id.inventoryTitle);
        title.setText(selectedCharName + "'s Inventory");

        final Button itemEntryButton = findViewById(R.id.itemEntryButton);
        itemEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String newItemName = itemName.getText().toString();
                String newItemWeight = itemWeight.getText().toString();
                String newItemCost = itemCost.getText().toString();

                dbHelper.addNewItem(selectedCharName, newItemName, newItemWeight, newItemCost, db);

                getCharacterInventory(selectedCharName);

            }
        });

        // TODO implement this later if I can figure it out
        /*final Button itemDeleteButton = findViewById(R.id.itemDeleteButton);
        itemEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String newItemName = itemName.getText().toString();
                String newItemWeight = itemWeight.getText().toString();
                String newItemCost = itemCost.getText().toString();

                dbHelper.addNewItem(selectedCharName, newItemName, newItemWeight, newItemCost, db);

                getCharacterInventory(selectedCharName);

            }
        });*/

        inventoryDisplay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                TextView itemView = inventoryDisplay.getChildAt(position).findViewById(R.id.idnum);
                String deleteItemID = itemView.getText().toString();
                TextView deletedItem = inventoryDisplay.getChildAt(position).findViewById(R.id.cName);
                String deletedItemShow = "Deleted " + deletedItem.getText().toString();

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                dbHelper.deleteItem(deleteItemID, db);
                Toast.makeText(view.getContext(), deletedItemShow, Toast.LENGTH_LONG).show();
                getCharacterInventory(selectedCharName);

                return true;
            }
        });

        getCharacterInventory(selectedCharName);


    }

    private void getTotalCharGold(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.fetchInventoryData(db, charName);
        Cursor cursor1 = db.rawQuery("SELECT SUM(ItemCost) FROM inventory WHERE CharacterName = '" + charName + "'", null);

        if(cursor1.moveToFirst()) {
           totalGold.setText(new String(String.valueOf(cursor1.getInt(0))));

        }



    }

    private void getTotalCharWeight(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.fetchInventoryData(db, charName);
        Cursor cursor1 = db.rawQuery("SELECT SUM(ItemWeight) FROM inventory WHERE CharacterName = '" + charName + "'", null);

        if(cursor1.moveToFirst()) {
            totalWeight.setText(new String(String.valueOf(cursor1.getInt(0))));

        }



    }

    private void getCharacterInventory(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.fetchInventoryData(db, charName);

        ListAdapter myAdapter = new SimpleCursorAdapter(this, R.layout.inventory_list_diplay,
                cursor,
                new String[]{InventoryTable.COLUMN_INVENTORY_ID, InventoryTable.COLUMN_ITEM_NAME, InventoryTable.COLUMN_ITEM_WEIGHT, InventoryTable.COLUMN_ITEM_COST},
                new int[]{R.id.idnum, R.id.cName, R.id.cWeight, R.id.cCost}, 0);
        inventoryDisplay.setAdapter(myAdapter);

        getTotalCharWeight(charName);
        getTotalCharGold(charName);


    }



}
