package com.zybooks.johnaustininventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.BCrypt; //Outside repository added for password encryption

//This class is used to create the database and tables for the inventory app
public class InventoryDatabase extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    private static InventoryDatabase mInventoryDb;

    public enum CategorySortOrder { ALPHABETIC, UPDATE_DESC, UPDATE_ASC }

    //checks if a database exists if not creates one
    public static InventoryDatabase getInstance(Context context) {
        if (mInventoryDb == null) {
            mInventoryDb = new InventoryDatabase(context);
        }
        return mInventoryDb;
    }

    //constructor
    private InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    //category database
    private static final class CategoryTable {
        private static final String TABLE = "categories";
        private static final String COL_NAME = "name";
        private static final String COL_UPDATE_TIME = "updated";
    }

    //item database
    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_CATEGORY = "category";
        private static final String COL_DESCRIPTION = "description";
    }

    //user database
    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create categories table
        db.execSQL("create table " + CategoryTable.TABLE + " (" +
                CategoryTable.COL_NAME + " primary key, " +
                CategoryTable.COL_UPDATE_TIME + " int)");

        // Create items table with foreign key that cascade deletes
        db.execSQL("create table " + ItemTable.TABLE + " (" +
                ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemTable.COL_NAME + ", " +
                ItemTable.COL_QUANTITY + ", " +
                ItemTable.COL_DESCRIPTION + ", " +
                ItemTable.COL_CATEGORY + ", " +
                "foreign key(" + ItemTable.COL_CATEGORY + ") references " +
                CategoryTable.TABLE + "(" + CategoryTable.COL_NAME + ") on delete cascade)");

        //creates user table
        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_USERNAME + " primary key, " +
                UserTable.COL_PASSWORD + " varchar(255))");



        // Add some default Categories
        String[] categories = { "Toys", "Electronics", "Clothing" };
        for (String cat: categories) {
            Category category = new Category(cat);
            ContentValues values = new ContentValues();
            values.put(CategoryTable.COL_NAME, category.getName());
            values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
            db.insert(CategoryTable.TABLE, null, values);
        }
    }

    @Override //drops tables if they exist
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CategoryTable.TABLE);
        db.execSQL("drop table if exists " + ItemTable.TABLE);
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    //adds a new item to the database
    public List<Category> getCategories(CategorySortOrder order) {
        List<Category> categories = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        // Order by name or update time
        String orderBy;
        switch (order) {
            case ALPHABETIC:
                orderBy = CategoryTable.COL_NAME + " collate nocase"; // order alphabetically
                break;
            case UPDATE_DESC:
                orderBy = CategoryTable.COL_UPDATE_TIME + " desc"; // order by most recently updated
                break;
            case UPDATE_ASC:
                orderBy = CategoryTable.COL_UPDATE_TIME + " asc"; // order by least recently updated
                break;
            default:
                orderBy = CategoryTable.COL_UPDATE_TIME; // order by most recently updated
                break;
        }

        // Get all categories
        String sql = "select * from " + CategoryTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                //add categories to the list
                Category category = new Category();
                category.setName(cursor.getString(0));
                category.setUpdateTime(cursor.getLong(1));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return categories;
    }

    //adds a new user to the database
    public boolean addUser(String username, String password) {

        // Hash the password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Add the user to the database
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);
        values.put(UserTable.COL_PASSWORD, hashedPassword);
        long id = db.insert(UserTable.TABLE, null, values);
        return id != -1;
    }


    //checks if a user exists
    public boolean checkUser() {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE, null);



        if (cursor.getCount() > 0) {
            return true;
        }

        cursor.close();
        return false;

    }

    // grants user login
    public boolean grantUserLogin(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE Username = ?", new String[] {username});

        // Check if the user exists
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();

            // Get the hashed password
            int passwordIndex = cursor.getColumnIndex(UserTable.COL_PASSWORD);

            // Check the password
            if (passwordIndex != -1) {
                String hashedPassword = cursor.getString(passwordIndex);
                return BCrypt.checkpw(password, hashedPassword);
            }
        }
        cursor.close();
        return false; // User does not exist
    }

    //adds a new category to the database
    public boolean addCategory(Category category) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getName());
        values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
        long id = db.insert(CategoryTable.TABLE, null, values);
        return id != -1;
    }

    // updates a category in the database
    public void updateCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getName());
        values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
        db.update(CategoryTable.TABLE, values,
                CategoryTable.COL_NAME + " = ?", new String[] { category.getName() });
    }

    // deletes a category from the database
    public void deleteCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CategoryTable.TABLE,
                CategoryTable.COL_NAME + " = ?", new String[] { category.getName() });
    }

    // returns a list of items in the database
    public List<Item> getItems(String category) {
        List<Item> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Get all items in the category database
        String sql = "select * from " + ItemTable.TABLE +
                " where " + ItemTable.COL_CATEGORY + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { category });
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setQuantity(cursor.getString(2));
                item.setDescription(cursor.getString(3));
                item.setCategory(cursor.getString(4));

                //add items to the list
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return items;
    }

    // returns a single item from the database
    public Item getItem(long itemId) {
        Item item = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + ItemTable.TABLE +
                " where " + ItemTable.COL_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Float.toString(itemId) });

        // Get the item
        if (cursor.moveToFirst()) {
            item = new Item();
            item.setId(cursor.getInt(0));
            item.setName(cursor.getString(1));
            item.setQuantity(cursor.getString(2));
            item.setDescription(cursor.getString(3));
            item.setCategory(cursor.getString(4));
        }
        cursor.close();
        return item;
    }

    // adds an item to the database
    public void addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, item.getName());
        values.put(ItemTable.COL_QUANTITY, item.getQuantity());
        values.put(ItemTable.COL_DESCRIPTION, item.getDescription());
        values.put(ItemTable.COL_CATEGORY, item.getCategory());
        long itemId = db.insert(ItemTable.TABLE, null, values);
        item.setId(itemId);

        // Change update time in Categories table
        updateCategory(new Category(item.getCategory()));
    }

    // updates an item in the database
    public void updateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ItemTable.COL_ID, item.getId());
        values.put(ItemTable.COL_NAME, item.getName());
        values.put(ItemTable.COL_QUANTITY, item.getQuantity());
        values.put(ItemTable.COL_DESCRIPTION, item.getDescription());
        values.put(ItemTable.COL_CATEGORY, item.getCategory());
        db.update(ItemTable.TABLE, values,
                ItemTable.COL_ID + " = " + item.getId(), null);

        // Change update time in Categories table
        updateCategory(new Category(item.getCategory()));
    }

    // deletes an item from the database
    public void deleteItem(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ItemTable.TABLE,
                ItemTable.COL_ID + " = " + itemId, null);
    }

}
