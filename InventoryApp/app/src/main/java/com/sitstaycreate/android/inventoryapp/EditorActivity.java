package com.sitstaycreate.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.sitstaycreate.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private boolean mProductHasChanged = false;
    private boolean mEditMode = false;
    private String name;
    private String price;
    private String quantityString;
    private String supplierName;
    private String supplierPhone;
    private Button incrementButton;
    private Button decrementButton;
    int currentQuantity;
    private Button orderButton;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);
        incrementButton = (Button) findViewById(R.id.increment_button);
        decrementButton = (Button) findViewById(R.id.decrement_button);
        orderButton = (Button) findViewById(R.id.order_button);

        //Get intent from CatalogActivity
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //If the Uri in the intent is null, this is creating a new entry in the database
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
            enableEditFields();
        } else {
            //If the Uri in the intent is not null, this is updating an entry in the database
            setTitle(getString(R.string.editor_activity_title_edit_product));
            //Call disableEditFields for detail view
            disableEditFields();

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        //On touch listeners determine whether or not data has been edited
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        //On click listener for incrementing quantity
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQuantityString = mQuantityEditText.getText().toString();
                //Check that quantity EditText element is not empty
                if(currentQuantityString.length()>0){
                    currentQuantity = Integer.parseInt(currentQuantityString);
                }
                //If empty, set currentQuantity to 0
                else {
                    currentQuantity = 0;
                }
                currentQuantity = ++currentQuantity;
                mQuantityEditText.setText(Integer.toString(currentQuantity));
            }
        });

        //On click listener for decrementing quantity
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQuantityString = mQuantityEditText.getText().toString();
                //Check that quantity EditText element is not empty
                if(currentQuantityString.length()>0){
                currentQuantity = Integer.parseInt(currentQuantityString);
                //Check that value is not 0
                if(currentQuantity>0){
                    currentQuantity = --currentQuantity;
                    mQuantityEditText.setText(Integer.toString(currentQuantity));
                    }
                else {
                    Toast.makeText(EditorActivity.this,
                            "Quantity cannot be less than 0.", Toast.LENGTH_SHORT).show();
                }
                }
                //If EditText is empty, set text to 0
                else{
                    mQuantityEditText.setText("0");
                    Toast.makeText(EditorActivity.this,
                            "Quantity cannot be less than 0.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //On click listener to send implicit intent with supplier phone number to call supplier
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierPhone));
                startActivity(intent);
            }
        });
    }


    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        String errorString = handleErrorsOnSave(nameString,
                priceString,
                quantityString,
                supplierNameString,
                supplierPhoneString);

        if (!(TextUtils.isEmpty(errorString))) {
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceString);
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);

        // Determine if this is a new or existing product by checking if
        // mCurrentProductUri is null or not
        //This is not an existing product
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            //If newUri is null, something went wrong with inserting the data
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            //This is an existing product
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri,
                    values,
                    null,
                    null);
            //If rowsAffected is 0, something went wrong with updating the data
            if (rowsAffected == 0) {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditorActivity.this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //This is a new product - hide menu options and buttons
        if (mCurrentProductUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
            MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
            MenuItem menuItemCancel = menu.findItem(R.id.action_cancel);
            menuItemDelete.setVisible(false);
            menuItemEdit.setVisible(false);
            menuItemCancel.setVisible(false);
            incrementButton.setVisibility(View.GONE);
            decrementButton.setVisibility(View.GONE);
            orderButton.setVisibility(View.GONE);
        }
        //This is an existing product and the user has clicked edit in the menu options
        //show/hide menu options
        else if (mEditMode == true) {
            MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
            MenuItem menuItemCancel = menu.findItem(R.id.action_cancel);
            menuItemEdit.setVisible(false);
            menuItemCancel.setVisible(true);
        }
        //This is an existing product and the user is at the detail view
        //show/hide menu options
        else {
            MenuItem menuItemEdit = menu.findItem(R.id.action_edit);
            MenuItem menuItemCancel = menu.findItem(R.id.action_cancel);
            menuItemEdit.setVisible(true);
            menuItemCancel.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save product to database
                saveProduct();
                return true;
            case R.id.action_edit:
                // Show editor fields
                mEditMode = true;
                invalidateOptionsMenu();
                enableEditFields();
                return true;
            case R.id.action_cancel:
                mEditMode = false;
                handleValuesOnCancel();
                invalidateOptionsMenu();
                disableEditFields();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //Do nothing if the cursor is no or less than one
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            // Extract out the value from the Cursor for the given column index
            name = cursor.getString(nameColumnIndex);
            price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            quantityString = Integer.toString(quantity);
            supplierName = cursor.getString(supplierNameColumnIndex);
            supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQuantityEditText.setText(quantityString);
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Enables edit fields when user selects edit option in menu
     */
    private void enableEditFields() {
        mNameEditText.setFocusable(true);
        mNameEditText.setFocusableInTouchMode(true);
        mNameEditText.setClickable(true);
        mNameEditText.setLongClickable(true);
        mPriceEditText.setFocusable(true);
        mPriceEditText.setFocusableInTouchMode(true);
        mPriceEditText.setClickable(true);
        mPriceEditText.setLongClickable(true);
        mQuantityEditText.setFocusable(true);
        mQuantityEditText.setFocusableInTouchMode(true);
        mQuantityEditText.setClickable(true);
        mQuantityEditText.setLongClickable(true);
        mSupplierNameEditText.setFocusable(true);
        mSupplierNameEditText.setFocusableInTouchMode(true);
        mSupplierNameEditText.setClickable(true);
        mSupplierNameEditText.setLongClickable(true);
        mSupplierPhoneEditText.setFocusable(true);
        mSupplierPhoneEditText.setFocusableInTouchMode(true);
        mSupplierPhoneEditText.setClickable(true);
        mSupplierPhoneEditText.setLongClickable(true);
    }

    /**
     * Reverts values in edit fields to data from Cursor if user clicks cancel
     */
    private void handleValuesOnCancel(){
        mNameEditText.setText(name);
        mPriceEditText.setText(price);
        mQuantityEditText.setText(quantityString);
        mSupplierNameEditText.setText(supplierName);
        mSupplierPhoneEditText.setText(supplierPhone);
    }

    private String handleErrorsOnSave(String name, String price, String quantity, String supplierName,
                                    String supplierPhone){
        String errorString = "";
        if (TextUtils.isEmpty(name)) {
                errorString = "Product name is required.";
        }

        if (TextUtils.isEmpty(price)) {
            if (TextUtils.isEmpty(errorString)) {
                errorString = "Price is required.";
            }
            else {
                errorString = errorString + "\nPrice is required.";
            }
        }

        if (TextUtils.isEmpty(quantity)) {
            if (TextUtils.isEmpty(errorString)) {
                errorString = "Quantity is required.";
            }
            else {
                errorString = errorString + "\nQuantity is required.";
            }
        }

        if (TextUtils.isEmpty(supplierName)) {
            if (TextUtils.isEmpty(errorString)) {
                errorString = "Supplier name is required.";
            }
            else {
                errorString = errorString + "\nSupplier name is required.";
            }
        }

        if (TextUtils.isEmpty(supplierPhone)) {
            if (TextUtils.isEmpty(errorString)) {
                errorString = "Supplier phone number is required.";
            }
            else {
                errorString = errorString + "\nSupplier phone number is required.";
            }
        }

        return errorString;

    }

    /**
     * Disable edit fields when user selects edit option in menu
     */
    private void disableEditFields() {
        mProductHasChanged = false;
        mNameEditText.setFocusable(false);
        mNameEditText.setClickable(false);
        mNameEditText.setLongClickable(false);
        mPriceEditText.setFocusable(false);
        mPriceEditText.setClickable(false);
        mPriceEditText.setLongClickable(false);
        mQuantityEditText.setFocusable(false);
        mQuantityEditText.setClickable(false);
        mQuantityEditText.setLongClickable(false);
        mSupplierNameEditText.setFocusable(false);
        mSupplierNameEditText.setClickable(false);
        mSupplierNameEditText.setLongClickable(false);
        mSupplierPhoneEditText.setFocusable(false);
        mSupplierPhoneEditText.setClickable(false);
        mSupplierPhoneEditText.setLongClickable(false);
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}