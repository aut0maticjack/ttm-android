package edu.cvtc.ttm.tabletopmanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
    EditText maxWeight;
    EditText goldOffset;
    TextView title;
    TextView totalGold;
    TextView totalWeight;
    TextView totalWeightLabel;
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
        totalGold = findViewById(R.id.goldAmtDisplay);
        totalWeight = findViewById(R.id.weightTrackerAmtDisplay);
        totalWeightLabel = findViewById(R.id.weightTrackerLabel);
        inventoryDisplay = findViewById(R.id.inventoryList);
        title = findViewById(R.id.inventoryTitle);
        title.setText(selectedCharName + "'s Inventory");
        maxWeight = findViewById(R.id.maxWeightEditText);
        goldOffset = findViewById(R.id.goldPlusMinusEditText);

        //takes the strings from the top three text boxes and uses them to create an item in the equipment table
        final Button itemEntryButton = findViewById(R.id.itemEntryButton);
        itemEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String newItemName = itemName.getText().toString();
                String newItemWeight = itemWeight.getText().toString();
                String newItemCost = itemCost.getText().toString();

                dbHelper.addNewItem(selectedCharName, newItemName, newItemWeight, newItemCost, db);

                updateUI(selectedCharName);
            }
        });

        //sets a new limit for the characters carrying capacity
        final Button updateMaxWeightButton = findViewById(R.id.updateMaxWeightButton);
        updateMaxWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String newMaxWeight = maxWeight.getText().toString();

                dbHelper.updateMaxWeight(db, selectedCharName, newMaxWeight);

                updateUI(selectedCharName);

            }
        });

        //decrease the characters net worth by the specified amount of gold
        final Button goldMinusButton = findViewById(R.id.goldMinusButton);
        goldMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String goldAmount = goldOffset.getText().toString();

                dbHelper.decreaseNetWorth(db, selectedCharName, goldAmount);

                updateUI(selectedCharName);

            }
        });

       //increase the characters net worth by the specified amount of gold
        final Button goldPlusButton = findViewById(R.id.goldPlusButton);
        goldPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String goldAmount = goldOffset.getText().toString();

                dbHelper.increaseNetWorth(db, selectedCharName, goldAmount);

                updateUI(selectedCharName);

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
                updateUI(selectedCharName);
                return true;
            }
        });

        updateUI(selectedCharName);

    }

    private String getMaxWeight(String charName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String str = "DB Error";

        Cursor cursor = dbHelper.fetchMaxWeight(db, charName);
        if (cursor.moveToFirst()) {
            str = cursor.getString(cursor.getColumnIndex("MaxWeight"));
            //String column_name = cursor.getColumnName(0);
            //Log.i("DB col name", "getMaxWeight: " + column_name);
            //Log.i("DB col name", "Should be: " + column_name);
        }
        return str;
    }

    private String getTotalCharGold(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String str = "DB Error";

        Cursor cursor = dbHelper.fetchNetWorth(db, charName);
        if (cursor.moveToFirst()) {
            str = cursor.getString(cursor.getColumnIndex("Gold"));
            //String column_name = cursor.getColumnName(0);
            //Log.i("DB col name", "Should be: " + column_name);
        }
        return str;
    }

    private String getTotalCharWeight(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.fetchWeightOfGear(db, charName);

        String str = "DB Error";
        if (cursor.moveToFirst()) {
            str = cursor.getString(cursor.getColumnIndex("SUM(ItemWeight)"));
            //String column_name = cursor.getColumnName(0);
            //Log.i("DB col name", "Should be: " + column_name);
        }
        return str;
    }

    private void getCharacterInventory(String charName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.fetchInventoryData(db, charName);

        ListAdapter myAdapter = new SimpleCursorAdapter(this, R.layout.inventory_list_diplay,
                cursor,
                new String[]{InventoryTable.COLUMN_INVENTORY_ID, InventoryTable.COLUMN_ITEM_NAME, InventoryTable.COLUMN_ITEM_WEIGHT, InventoryTable.COLUMN_ITEM_COST},
                new int[]{R.id.idnum, R.id.cName, R.id.cWeight, R.id.cCost}, 0);
        inventoryDisplay.setAdapter(myAdapter);


    }

    private void updateUI(String charName){
        getCharacterInventory(charName);
        maxWeight.setText(getMaxWeight(charName));
        totalGold.setText(getTotalCharGold(charName));
        totalWeight.setText(getTotalCharWeight(charName));
        checkIfOverWeightLimit();
    }

   private void checkIfOverWeightLimit(){
        //Why am I storing data in the UI? I'm not really...(it should be refreshed continuously)
        //however, I am pulling these straight from the UI rather than the variables or
        //stable storage because I think that UI consistency is more important than the correctness
        //of any one piece of the UI

       int current = 0;
       int max = 0;

       try {
           current = Integer.parseInt(totalWeight.getText().toString());
       } catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }

       try {
           max = Integer.parseInt(maxWeight.getText().toString());
       } catch(NumberFormatException nfe) {
           System.out.println("Could not parse " + nfe);
       }

       if(current > max){
           totalWeight.setTextColor(Color.RED);
           totalWeightLabel.setTextColor(Color.RED);

        }
        else {
           totalWeight.setTextColor(Color.BLACK);
           totalWeightLabel.setTextColor(Color.BLACK);

        }
    }



}
