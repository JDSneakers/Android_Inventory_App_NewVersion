package com.zybooks.johnaustininventoryapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ItemEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.zybooks.johnaustininventoryapp.item_id";
    public static final String EXTRA_CATEGORY = "com.zybooks.johnaustininventoryapp.category";

    private EditText mItemText;
    private EditText mQuantityText;
    private EditText mDescriptionText;

    private InventoryDatabase mInventoryDb;
    private long mItemId;
    private Item mItem;

    @Override //creates the item edit activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        mItemText = findViewById(R.id.itemText);
        mQuantityText = findViewById(R.id.quantityText);
        mDescriptionText = findViewById(R.id.descriptionText);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        // Get item ID from ItemActivity
        Intent intent = getIntent();
        mItemId = intent.getLongExtra(EXTRA_ITEM_ID, -1);

        // If mItemId is -1, then this is a new Item
        if (mItemId == -1) {
            // Add new Item
            mItem = new Item();
            setTitle(R.string.add_item);
        }
        else { // If mItemId is not -1, then this is an existing Item
            // Update existing Item
            mItem = mInventoryDb.getItem(mItemId);
            mItemText.setText(mItem.getName());
            mQuantityText.setText(mItem.getQuantity());
            mDescriptionText.setText(mItem.getDescription());
            setTitle(R.string.update_item);
        }
        // Get category from ItemActivity
        String category = intent.getStringExtra(EXTRA_CATEGORY);
        mItem.setCategory(category);
    }

    // Save Item button was clicked
    public void saveButtonClick(View view) {

        // Update Item object with user input
        mItem.setName(mItemText.getText().toString());
        mItem.setQuantity(mQuantityText.getText().toString());
        mItem.setDescription(mDescriptionText.getText().toString());

        // Validate user input
        if (mItemText.getText().toString().equals("") || mQuantityText.getText().toString().equals("") || mDescriptionText.getText().toString().equals("")) {
            Toast.makeText(ItemEditActivity.this, "No Field Can Be Left Blank", Toast.LENGTH_LONG).show();
        }
        else { // User input is valid
            if (mItemId == -1) {
                // New Item
                mInventoryDb.addItem(mItem);
            } else {
                // Existing Item
                mInventoryDb.updateItem(mItem);
            }

            // Send back Item ID
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ITEM_ID, mItem.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}