package com.zybooks.johnaustininventoryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.ActionMode;
import android.graphics.Color;

public class CategoryActivity extends AppCompatActivity
        implements CategoryDialogFragment.OnCategoryEnteredListener {

    private InventoryDatabase mInventoryDb;
    private CategoryAdapter mCategoryAdapter;
    private RecyclerView mRecyclerView;
    private int[] mCategoryColors;

    private Category mSelectedCategory;
    private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    private final int REQUEST_SMS_CODE = 0;

    //create category function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mCategoryColors = getResources().getIntArray(R.array.categoryColors);

        // create category database instance
        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        //allows for items to be viewed if off the page by scrolling through categories if needed
        mRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Create 2 grid layout columns
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Shows the available Categories
        mCategoryAdapter = new CategoryAdapter(loadCategories());
        mRecyclerView.setAdapter(mCategoryAdapter);

        //user prompt to approve sms permissions if they have not already
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS )) {
                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.SEND_SMS }, REQUEST_SMS_CODE);

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu for the app bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Determine which app bar item was chosen
        switch (item.getItemId()) {

            //allows the categories to be sorted by the user either alphabetically or based on the time created
            case R.id.alphabetical:
                mCategoryAdapter = new CategoryAdapter(loadAlphabeticalCategories());
                mRecyclerView.setAdapter(mCategoryAdapter);
                return true;
            case R.id.ascending:
                mCategoryAdapter = new CategoryAdapter(loadAscendingCategories());
                mRecyclerView.setAdapter(mCategoryAdapter);
                return true;
            case R.id.descending:
                mCategoryAdapter = new CategoryAdapter(loadDescendingCategories());
                mRecyclerView.setAdapter(mCategoryAdapter);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //loads categories in alphabetic order
    private List<Category> loadAlphabeticalCategories() {
        return mInventoryDb.getCategories(InventoryDatabase.CategorySortOrder.ALPHABETIC);
    }

    //loads categories in order of what was created first to last
    private List<Category> loadAscendingCategories() {
        return mInventoryDb.getCategories(InventoryDatabase.CategorySortOrder.UPDATE_ASC);
    }

    //loads categories in order of what was created last to first
    private List<Category> loadDescendingCategories() {
        return mInventoryDb.getCategories(InventoryDatabase.CategorySortOrder.UPDATE_DESC);
    }

    //checks result of user from the sms permission prompt
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_SMS_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted!
                    Toast.makeText(CategoryActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    // Permission denied!
                    Toast.makeText(CategoryActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onCategoryEntered(String category) {

        // Returns category entered in the CategoryDialogFragment dialog
        if (category.length() > 0) {
            //assigns user input to a new category object
            Category cat = new Category(category);
            if (mInventoryDb.addCategory(cat)) {
                //adds new category to app
                mCategoryAdapter.addCategory(cat);
                Toast.makeText(this, "Added " + category, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.category_exists, category);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addCategoryClick(View view) {

        // Prompt user to type new Category in pop up window
        FragmentManager manager = getSupportFragmentManager();
        CategoryDialogFragment dialog = new CategoryDialogFragment();
        dialog.show(manager, "CategoryDialog");
    }

    //loads categories in order of what was created first to last
    private List<Category> loadCategories() {
        return mInventoryDb.getCategories(InventoryDatabase.CategorySortOrder.UPDATE_DESC);
    }


    //handles logic for categories including colors and actions
    //taken when clicked or clicked and held
    private class CategoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Category mCategory;
        private TextView mTextView;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.categoryTextView);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Category category, int position) {
            mCategory = category;
            mTextView.setText(category.getName());

            if (mSelectedCategoryPosition == position) {
                // Make selected category stand out
                mTextView.setBackgroundColor(Color.RED);
            }
            else {
                // Make the background color dependent on the length of the Category string
                int colorIndex = category.getName().length() % mCategoryColors.length;
                mTextView.setBackgroundColor(mCategoryColors[colorIndex]);
            }
        }

        //changes to the item page for the chosen category
        @Override
        public void onClick(View view) {
            // Start ItemActivity, indicating what Category was clicked
            Intent intent = new Intent(CategoryActivity.this, ItemActivity.class);
            intent.putExtra(ItemActivity.EXTRA_CATEGORY, mCategory.getName());
            startActivity(intent);
        }

        //allows user the option to remove a category if category
        //is pressed and held
        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }

            mSelectedCategory = mCategory;
            mSelectedCategoryPosition = getAdapterPosition();

            // Re-bind the selected item
            mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);

            // Show the CAB
            mActionMode = CategoryActivity.this.startActionMode(mActionModeCallback);

            return true;
        }
    }

    //manages the data and UI interactions for the RecyclerView
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private List<Category> mCategoryList;

        public CategoryAdapter(List<Category> categories) {
            mCategoryList = categories;
        }

        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CategoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position){
            holder.bind(mCategoryList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCategoryList.size();
        }

        public void addCategory(Category category) {
            // Add the new category at the beginning of the list
            mCategoryList.add(0, category);

            // Notify the adapter that item was added to the beginning of the list
            notifyItemInserted(0);

            // Scroll to the top
            mRecyclerView.scrollToPosition(0);
        }

        public void removeCategory(Category category) {
            // Find Category in the list
            int index = mCategoryList.indexOf(category);
            if (index >= 0) {
                // Remove the category
                mCategoryList.remove(index);

                // Notify adapter of category removal
                notifyItemRemoved(index);
            }
        }
    }

    //handles the actions within the contextual action bar and provides specific behavior
    //for the CAB-related actions.
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Provide context menu for CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Process action item selection
            switch (item.getItemId()) {
                case R.id.delete:
                    // Delete from the database and remove from the RecyclerView
                    mInventoryDb.deleteCategory(mSelectedCategory);
                    mCategoryAdapter.removeCategory(mSelectedCategory);

                    // Close the CAB
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;

            // CAB closing, need to deselect item if not deleted
            mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);
            mSelectedCategoryPosition = RecyclerView.NO_POSITION;
        }
    };
}