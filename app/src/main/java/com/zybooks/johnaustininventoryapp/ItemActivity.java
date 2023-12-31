package com.zybooks.johnaustininventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

// Activity to display items in a category
public class ItemActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "com.zybooks.johnaustininventoryapp.category";

    private InventoryDatabase mInventoryDb;
    private String mCategory;
    private List<Item> mItemList;
    private TextView mQuantityLabel;
    private TextView mQuantityText;
    private TextView mItemText;
    private TextView mDescriptionLabel;
    private TextView mDescriptionText;
    private int mCurrentItemIndex;
    private ViewGroup mShowItemsLayout;
    private ViewGroup mNoItemsLayout;

    private final int REQUEST_CODE_NEW_ITEM = 0;
    private final int REQUEST_CODE_UPDATE_ITEM = 1;

    private Item mDeletedItem;

    @Override //creates the item activity
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        // Hosting activity provides the Category of the Items to display
        Intent intent = getIntent();
        mCategory = intent.getStringExtra(EXTRA_CATEGORY);

        // Load all items for this category
        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());
        mItemList = mInventoryDb.getItems(mCategory);

        //loads the item object for chosen category
        mItemText = findViewById(R.id.itemText);
        mQuantityLabel = findViewById(R.id.quantityLabel);
        mQuantityText = findViewById(R.id.quantityText);
        mDescriptionLabel = findViewById(R.id.descriptionLabel);
        mDescriptionText = findViewById(R.id.descriptionText);
        mShowItemsLayout = findViewById(R.id.showItemsLayout);
        mNoItemsLayout = findViewById(R.id.noItemsLayout);

        // Show first item
        showItem(0);
    }

    //function for determining if there are items to display
    //if so displays if not does not
    @Override
    protected void onStart() {
        super.onStart();

        // Are there items to display?
        if (mItemList.size() == 0) {
            updateAppBarTitle();
            displayItem(false);
        } else {
            displayItem(true);
        }
    }

    @Override //function for when the user returns to the item activity
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu, menu);
        return true;
    }

    //logic for item functionality
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine which app bar item was chosen
        switch (item.getItemId()) {
            //allows user to cycle through items in the category
            case R.id.previous: // Display previous item
                showItem(mCurrentItemIndex - 1);
                return true;
            case R.id.next: // Display next item
                showItem(mCurrentItemIndex + 1);
                return true;

            //allows the items to be added, edited, or deleted by the user
            case R.id.add:
                addItem(); // Add new item
                return true;
            case R.id.edit:
                editItem(); // Edit current item
                return true;
            case R.id.delete:
                deleteItem(); // Delete current item
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //function for adding item object to item list on screen
    public void addItemButtonClick(View view) {
        addItem();
    }


    // Show or hide the appropriate screen
    private void displayItem(boolean display) {

        if (display) {
            mShowItemsLayout.setVisibility(View.VISIBLE);
            mNoItemsLayout.setVisibility(View.GONE);
        } else {
            mShowItemsLayout.setVisibility(View.GONE);
            mNoItemsLayout.setVisibility(View.VISIBLE);
        }
    }

    // Display category and number of items in app bar
    private void updateAppBarTitle() {

        ActionBar actionBar = getSupportActionBar();

        // if the action bar is not null, set the title
        if (actionBar != null) {
            String title = getResources().getString(R.string.item_number,
                    mCategory, mCurrentItemIndex + 1, mItemList.size());
            setTitle(title);
        }
    }

    //logic for the user to add an item to the list
    private void addItem() {
        Intent intent = new Intent(this, ItemEditActivity.class);
        intent.putExtra(ItemEditActivity.EXTRA_CATEGORY, mCategory);
        startActivityForResult(intent, REQUEST_CODE_NEW_ITEM);
    }

    //logic for the user to edit an item on the list
    private void editItem() {

        // Make sure there is an item to edit
        if (mCurrentItemIndex >= 0) {
            Intent intent = new Intent(this, ItemEditActivity.class);
            intent.putExtra(EXTRA_CATEGORY, mCategory);
            long itemId = mItemList.get(mCurrentItemIndex).getId();
            intent.putExtra(ItemEditActivity.EXTRA_ITEM_ID, itemId);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ITEM);
        }
    }

    //logic for the user to delete an item on the list
    private void deleteItem() {
        if (mCurrentItemIndex >= 0) {

            // Save item in case user undoes delete
            mDeletedItem = mItemList.get(mCurrentItemIndex);

            // Delete item from database and list
            long itemId = mItemList.get(mCurrentItemIndex).getId();
            mInventoryDb.deleteItem(mDeletedItem.getId());
            mItemList.remove(mCurrentItemIndex);

            // Update the screen
            if (mItemList.size() == 0) {
                // No items left to show
                mCurrentItemIndex = -1;
                updateAppBarTitle();
                displayItem(false);
            }
            else { // Show previous or next item
                showItem(mCurrentItemIndex);
            }

            // Show delete message with Undo button
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.item_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {

                // Add item back
                @Override
                public void onClick(View v) {
                    mInventoryDb.addItem(mDeletedItem);
                    mItemList.add(mDeletedItem);
                    showItem(mItemList.size() - 1);
                    displayItem(true);
                }
            });
            snackbar.show();
        }
    }

    // Show item at given index
    private void showItem(int itemIndex) {

        // check if there are items to show in the list and if so show them
        // if the item index is less than 0, set the item index to the last item in the list
        if (mItemList.size() > 0) {
            if (itemIndex < 0) {
                itemIndex = mItemList.size() - 1;
            } else if (itemIndex >= mItemList.size()) {
                itemIndex = 0;
            }

            // Show item info
            mCurrentItemIndex = itemIndex;
            updateAppBarTitle();

            // Get item from database
            Item item = mItemList.get(mCurrentItemIndex);
            mItemText.setText(item.getName());
            mQuantityText.setText(item.getQuantity());
            mDescriptionText.setText(item.getDescription());

        }
        else {
            // No items yet
            mCurrentItemIndex = -1;
        }
    }

    @Override //function for when the user returns to the item activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if item was added or updated
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NEW_ITEM) {
            // Get added item
            long itemId = data.getLongExtra(ItemEditActivity.EXTRA_ITEM_ID, -1);
            Item newItem = mInventoryDb.getItem(itemId);

            // Add newly created item to the item list and show it
            mItemList.add(newItem);
            showItem(mItemList.size() - 1);

            // Show message
            Toast.makeText(this, R.string.item_added, Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_UPDATE_ITEM) {
            // Get updated item
            long itemId = data.getLongExtra(ItemEditActivity.EXTRA_ITEM_ID, -1);
            Item updatedItem = mInventoryDb.getItem(itemId);

            // Replace current item in item list with updated item
            Item currentItem = mItemList.get(mCurrentItemIndex);
            currentItem.setName(updatedItem.getName());
            currentItem.setQuantity(updatedItem.getQuantity());
            currentItem.setDescription(updatedItem.getDescription());
            showItem(mCurrentItemIndex);

            // Show message
            Toast.makeText(this, R.string.item_updated, Toast.LENGTH_SHORT).show();
        }
    }

}