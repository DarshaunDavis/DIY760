package com.achieve760.diy760;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GenerateFragment extends Fragment {
    private static final int NUM_SECTIONS = 12;
    private RadioGroup section4RadioGroup;
    private RadioGroup section5RadioGroup;
    private RadioGroup section6RadioGroup;
    private RadioGroup section7RadioGroup;
    private RadioGroup section8RadioGroup;
    private RadioGroup section9RadioGroup;
    private RadioGroup section10RadioGroup;
    private RadioGroup section12RadioGroup;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText addressEditText;
    private EditText apartmentEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipEditText;
    private EditText socialEditText;
    private EditText documentEditText;
    private Spinner section2Spinner;
    private Button datePickerButton1;
    private Button datePickerButton4;
    private Button nameAddButton;
    private Button addressAddButton;
    private Button accountsAddButton;
    private Button inquiriesAddButton;
    private Button recordsAddButton;
    private Button documentsAddButton;
    private TableRow addressRow;
    private TableRow accountsRow;
    private TableRow inquiriesRow;
    private TableRow recordsRow;
    private TableRow documentsRow;
    private TableLayout nameFormTable;
    private TableLayout addressFormTable;
    private TableLayout accountsFormTable;
    private TableLayout inquiriesFormTable;
    private TableLayout recordsFormTable;
    private TableLayout documentsFormTable;
    private LinearLayout nameTextFieldsContainer;
    private LinearLayout addressTextFieldsContainer;
    private LinearLayout accountsTextFieldsContainer;
    private LinearLayout inquiriesTextFieldsContainer;
    private LinearLayout recordsTextFieldsContainer;
    private UnsavedChangesCallback callback;
    private boolean hasUnsavedChanges;
    private PrintedPdfDocument pdfDocument;
    private int totalPages;
    private SparseIntArray writtenPagesArray;
    private PrintAttributes currentAttributes;
    private InterstitialAd mInterstitialAd;
    private int mGenerateButtonClickCount = 0;
    private boolean birthdateSelected = false;
    private boolean letterdateSelected = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (UnsavedChangesCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement UnsavedChangesCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    // Call this method whenever a txt is generated
    private void txtGenerated() {
        hasUnsavedChanges = true;
        callback.onUnsavedChanges(true);
    }

    // Call this method whenever the changes are saved or discarded
    private void changesSavedOrDiscarded() {
        hasUnsavedChanges = false;
        callback.onUnsavedChanges(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate, container, false);

        Button generateButton = view.findViewById(R.id.generateButton);
        Button clearButton = view.findViewById(R.id.clearButton);
        TextView generatedContent = view.findViewById(R.id.generatedContent);
        Button printButton = view.findViewById(R.id.printButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        datePickerButton1 = view.findViewById(R.id.datePickerButton);
        setupDatePickerButton(datePickerButton1, true);
        Button datePickerButton2 = (Button) view.findViewById(R.id.inquiry_date);
        setupDatePickerButton(datePickerButton2, false);
        Button datePickerButton3 = (Button) view.findViewById(R.id.record_date);
        setupDatePickerButton(datePickerButton3, false);
        datePickerButton4 = view.findViewById(R.id.birthDatePickerButton);
        setupDatePickerButton(datePickerButton4, false);

        section4RadioGroup = view.findViewById(R.id.radioGroup4);
        section5RadioGroup = view.findViewById(R.id.radioGroup5);
        section6RadioGroup = view.findViewById(R.id.radioGroup6);
        section7RadioGroup = view.findViewById(R.id.radioGroup7);
        section8RadioGroup = view.findViewById(R.id.radioGroup8);
        section9RadioGroup = view.findViewById(R.id.radioGroup9);
        section10RadioGroup = view.findViewById(R.id.radioGroup10);
        section12RadioGroup = view.findViewById(R.id.radioGroup12);

        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        apartmentEditText = view.findViewById(R.id.apartmentEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        stateEditText = view.findViewById(R.id.stateEditText);
        zipEditText = view.findViewById(R.id.zipEditText);
        socialEditText = view.findViewById(R.id.socialEditText);
        documentEditText = view.findViewById(R.id.documentEditText);

        //nameRow = view.findViewById(R.id.nameRow);
        addressRow = view.findViewById(R.id.addressRow);
        accountsRow = view.findViewById(R.id.accountsRow);
        inquiriesRow = view.findViewById(R.id.inquiriesRow);
        recordsRow = view.findViewById(R.id.recordsRow);

        //nameTextFieldsContainer = view.findViewById(R.id.name_text_fields_container);
        addressTextFieldsContainer = view.findViewById(R.id.address_text_fields_container);
        accountsTextFieldsContainer = view.findViewById(R.id.accounts_text_fields_container);
        inquiriesTextFieldsContainer = view.findViewById(R.id.inquiries_text_fields_container);
        recordsTextFieldsContainer = view.findViewById(R.id.records_text_fields_container);

        nameAddButton = view.findViewById(R.id.name_add_button);
        addressAddButton = view.findViewById(R.id.address_add_button);
        accountsAddButton = view.findViewById(R.id.accounts_add_button);
        inquiriesAddButton = view.findViewById(R.id.inquiries_add_button);
        recordsAddButton = view.findViewById(R.id.records_add_button);
        documentsAddButton = view.findViewById(R.id.documents_add_button);

        nameFormTable = view.findViewById(R.id.name_form_table);
        addressFormTable = view.findViewById(R.id.address_form_table);
        accountsFormTable = view.findViewById(R.id.accounts_form_table);
        inquiriesFormTable = view.findViewById(R.id.inquiries_form_table);
        recordsFormTable = view.findViewById(R.id.records_form_table);
        documentsFormTable = view.findViewById(R.id.documents_form_table);

        // Pass a flag to the setupDatePickerButton function
        // The flag indicates whether the button is for the birthdate or the letter date
        setupDatePickerButton(datePickerButton1, false);
        setupDatePickerButton(datePickerButton4, true);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add date validation here
                if (!birthdateSelected && !letterdateSelected) {
                    Toast.makeText(getContext(), "Please select both dates", Toast.LENGTH_LONG).show();
                    return;
                } else if (!birthdateSelected) {
                    Toast.makeText(getContext(), "Please select a birthdate", Toast.LENGTH_LONG).show();
                    return;
                } else if (!letterdateSelected) {
                    Toast.makeText(getContext(), "Please select a letter date", Toast.LENGTH_LONG).show();
                    return;
                }

                // Continue with existing validation
                if (validateForm()) {
                    // Increment the click count
                    mGenerateButtonClickCount++;

                    // Check if this is the third click
                    if (mGenerateButtonClickCount % 3 == 0) {
                        // Check if the interstitial ad has been loaded
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                        // Load a new interstitial ad for the next time
                        AdRequest adRequest = new AdRequest.Builder().build();
                        InterstitialAd.load(
                                getActivity(),
                                "ca-app-pub-1533970296877426/6444548216",
                                adRequest,
                                new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        // The mInterstitialAd reference will be null until
                                        // an ad is loaded.
                                        mInterstitialAd = interstitialAd;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        // Handle the error
                                        Log.i(TAG, loadAdError.getMessage());
                                        mInterstitialAd = null;
                                    }
                                });
                    }

                    String fileContents = generateRandomLetter();
                    generatedContent.setText(fileContents);
                    printButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    clearButton.setVisibility(View.VISIBLE);
                }
            }
        });


        /*saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter filename:");

                // Set up the input field
                final EditText input = new EditText(getActivity());
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = input.getText().toString() + ".txt";
                        File file = new File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(generatedContent.getText().toString().getBytes());
                            Toast.makeText(getActivity(), "Saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "Error saving file", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });*/

        //Temporary Save Button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Save Feature is Coming Soon!", Toast.LENGTH_SHORT).show();
                }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the content in the TextView
                generatedContent.setText("");

                // Hide the print, save, and clear buttons
                printButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String letterContent = generatedContent.getText().toString();
                doPrint(letterContent);
            }
        });


        nameAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows4();
            }
        });

        addressAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows5();
            }
        });

        accountsAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows6();
            }
        });

        inquiriesAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows7();
            }
        });

        recordsAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows8();
            }
        });

        documentsAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows12();
            }
        });

        section2Spinner = view.findViewById(R.id.section2Spinner);
        ArrayAdapter<CharSequence> section2Adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.section2_options,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        section2Adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        section2Spinner.setAdapter(section2Adapter);

        Spinner nameSpinner = view.findViewById(R.id.name_spinner);
        ArrayAdapter<CharSequence> nameAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.name_dispute_options, android.R.layout.simple_spinner_item);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(nameAdapter);

        Spinner addressSpinner = view.findViewById(R.id.address_spinner);
        ArrayAdapter<CharSequence> addressAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.address_dispute_options, android.R.layout.simple_spinner_item);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressAdapter);

        Spinner accountsSpinner = view.findViewById(R.id.accounts_spinner);
        ArrayAdapter<CharSequence> accountsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.accounts_dispute_options, android.R.layout.simple_spinner_item);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountsAdapter);

        Spinner inquiriesSpinner = view.findViewById(R.id.inquiries_spinner);
        ArrayAdapter<CharSequence> inquiriesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.inquiries_dispute_options, android.R.layout.simple_spinner_item);
        inquiriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inquiriesSpinner.setAdapter(inquiriesAdapter);

        Spinner recordsSpinner = view.findViewById(R.id.records_spinner);
        ArrayAdapter<CharSequence> recordsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.records_dispute_options, android.R.layout.simple_spinner_item);
        recordsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recordsSpinner.setAdapter(recordsAdapter);

        section4RadioGroup = view.findViewById(R.id.radioGroup4);
        section4RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId4) {
                if (checkedId4 == R.id.radioButton4Yes) {
                    for (int i = 0; i < nameFormTable.getChildCount(); i++) {
                        View child = nameFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < nameFormTable.getChildCount(); i++) {
                        View child = nameFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        section5RadioGroup = view.findViewById(R.id.radioGroup5);
        section5RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId5) {
                if (checkedId5 == R.id.radioButton5Yes) {
                    for (int i = 0; i < addressFormTable.getChildCount(); i++) {
                        View child = addressFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < addressFormTable.getChildCount(); i++) {
                        View child = addressFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        section6RadioGroup = view.findViewById(R.id.radioGroup6);
        section6RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId6) {
                if (checkedId6 == R.id.radioButton6Yes) {
                    for (int i = 0; i < accountsFormTable.getChildCount(); i++) {
                        View child = accountsFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < accountsFormTable.getChildCount(); i++) {
                        View child = accountsFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        section7RadioGroup = view.findViewById(R.id.radioGroup7);
        section7RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId7) {
                if (checkedId7 == R.id.radioButton7Yes) {
                    for (int i = 0; i < inquiriesFormTable.getChildCount(); i++) {
                        View child = inquiriesFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < inquiriesFormTable.getChildCount(); i++) {
                        View child = inquiriesFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        section8RadioGroup = view.findViewById(R.id.radioGroup8);
        section8RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId8) {
                if (checkedId8 == R.id.radioButton8Yes) {
                    for (int i = 0; i < recordsFormTable.getChildCount(); i++) {
                        View child = recordsFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < recordsFormTable.getChildCount(); i++) {
                        View child = recordsFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        section12RadioGroup = view.findViewById(R.id.radioGroup12);
        section12RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId12) {
                if (checkedId12 == R.id.radioButton12Yes) {
                    for (int i = 0; i < documentsFormTable.getChildCount(); i++) {
                        View child = documentsFormTable.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (int i = 0; i < documentsFormTable.getChildCount(); i++) {
                        View child = documentsFormTable.getChildAt(i);
                        child.setVisibility(View.GONE);
                    }
                }
            }
        });

        // Load an Interstitial Ad
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                getActivity(),
                "ca-app-pub-1533970296877426/6444548216",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

        return view;
    }

    private void createNewRows4() {
        // Create new TableRows and Views
        final TableRow newRow1 = new TableRow(getContext());
        final TableRow newRow2 = new TableRow(getContext());

        EditText newName = new EditText(getContext());
        newName.setHint("Inaccurate Name");

        Spinner newSpinner = new Spinner(getContext());
        ArrayAdapter<CharSequence> nameAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.name_dispute_options, android.R.layout.simple_spinner_item);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(nameAdapter);

        Button newAddButton4 = new Button(getContext());
        newAddButton4.setText("+");
        newAddButton4.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton4.setBackgroundResource(R.color.colorPrimary);

        Button newRemoveButton4 = new Button(getContext());
        newRemoveButton4.setText("-");
        newRemoveButton4.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton4.setBackgroundResource(R.color.colorPrimary);

        // Set the layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params3 = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 8f);
        newName.setLayoutParams(params1);
        newSpinner.setLayoutParams(params3);

        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels
        newAddButton4.setLayoutParams(buttonParams);
        newRemoveButton4.setLayoutParams(buttonParams);

        newAddButton4.setBackgroundResource(R.drawable.rounded_button);
        newRemoveButton4.setBackgroundResource(R.drawable.rounded_button);

        // Add the Views to the first TableRow
        newRow1.addView(newName);

        // Add the Spinner and Buttons to the second TableRow
        newRow2.addView(newSpinner);
        newRow2.addView(newAddButton4);
        newRow2.addView(newRemoveButton4);

        // Add the TableRows to the TableLayout
        nameFormTable.addView(newRow1);
        nameFormTable.addView(newRow2);

        // Set onClickListener for the new + button
        newAddButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows4();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the added rows
                nameFormTable.removeView(newRow1);
                nameFormTable.removeView(newRow2);
            }
        });
    }

    private void createNewRows5() {
        // Create new TableRows and Views
        final TableRow newRow1 = new TableRow(getContext());
        final TableRow newRow2 = new TableRow(getContext());
        final TableRow newRow3 = new TableRow(getContext());

        // Row 1
        EditText newAddress = new EditText(getContext());
        newAddress.setHint("Address");
        EditText newApartment = new EditText(getContext());
        newApartment.setHint("Apartment");

        // Row 2
        EditText newCity = new EditText(getContext());
        newCity.setHint("City");
        EditText newState = new EditText(getContext());
        newState.setHint("State");
        EditText newZip = new EditText(getContext());
        newZip.setHint("Zip");

        // Row 3
        Spinner newSpinner = new Spinner(getContext());
        // Assuming you have a array for your spinner options, replace 'R.array.your_array' with your array's id
        ArrayAdapter<CharSequence> addressAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.address_dispute_options, android.R.layout.simple_spinner_item);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(addressAdapter);

        Button newAddButton5 = new Button(getContext());
        newAddButton5.setText("+");
        newAddButton5.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton5.setBackgroundResource(R.color.colorPrimary);

        Button newRemoveButton5 = new Button(getContext());
        newRemoveButton5.setText("-");
        newRemoveButton5.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton5.setBackgroundResource(R.color.colorPrimary);

        // Set layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams params3 = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 8f);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels
        newAddButton5.setBackgroundResource(R.drawable.rounded_button);
        newRemoveButton5.setBackgroundResource(R.drawable.rounded_button);

        newAddress.setLayoutParams(params1);
        newApartment.setLayoutParams(params1);
        newCity.setLayoutParams(params1);
        newState.setLayoutParams(params1);
        newZip.setLayoutParams(params1);
        newSpinner.setLayoutParams(params3);
        newAddButton5.setLayoutParams(buttonParams);
        newRemoveButton5.setLayoutParams(buttonParams);

        newRow1.addView(newAddress);
        newRow1.addView(newApartment);

        newRow2.addView(newCity);
        newRow2.addView(newState);
        newRow2.addView(newZip);

        newRow3.addView(newSpinner);
        newRow3.addView(newAddButton5);
        newRow3.addView(newRemoveButton5);

        // Add the TableRows to the TableLayout
        addressFormTable.addView(newRow1);
        addressFormTable.addView(newRow2);
        addressFormTable.addView(newRow3);

        // Set onClickListener for the new + button
        newAddButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows5();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the added rows
                addressFormTable.removeView(newRow1);
                addressFormTable.removeView(newRow2);
                addressFormTable.removeView(newRow3);
            }
        });
    }

    private void createNewRows6() {
        // Create new TableRows and Views
        final TableRow newRow1 = new TableRow(getContext());
        final TableRow newRow2 = new TableRow(getContext());

        // Row 1
        EditText newAccountName = new EditText(getContext());
        newAccountName.setHint("Account Name");
        EditText newAccountNumber = new EditText(getContext());
        newAccountNumber.setHint("Account Number");

        // Row 2
        Spinner newSpinner = new Spinner(getContext());
        // Assuming you have a array for your spinner options, replace 'R.array.your_array' with your array's id
        ArrayAdapter<CharSequence> accountsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.accounts_dispute_options, android.R.layout.simple_spinner_item);
        accountsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(accountsAdapter);

        Button newAddButton6 = new Button(getContext());
        newAddButton6.setText("+");
        newAddButton6.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton6.setBackgroundResource(R.color.colorPrimary);

        Button newRemoveButton6 = new Button(getContext());
        newRemoveButton6.setText("-");
        newRemoveButton6.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton6.setBackgroundResource(R.color.colorPrimary);

        // Set layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams spinnerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 8f);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels
        newAddButton6.setBackgroundResource(R.drawable.rounded_button);
        newRemoveButton6.setBackgroundResource(R.drawable.rounded_button);

        newAccountName.setLayoutParams(params1);
        newAccountNumber.setLayoutParams(params1);
        newSpinner.setLayoutParams(spinnerParams);
        newAddButton6.setLayoutParams(buttonParams);
        newRemoveButton6.setLayoutParams(buttonParams);

        newRow1.addView(newAccountName);
        newRow1.addView(newAccountNumber);

        newRow2.addView(newSpinner);
        newRow2.addView(newAddButton6);
        newRow2.addView(newRemoveButton6);

        // Add the TableRows to the TableLayout
        accountsFormTable.addView(newRow1);
        accountsFormTable.addView(newRow2);

        // Set onClickListener for the new + button
        newAddButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows6();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the added rows
                accountsFormTable.removeView(newRow1);
                accountsFormTable.removeView(newRow2);
            }
        });
    }

    private void createNewRows7() {
        // Create new TableRows and Views
        final TableRow newRow1 = new TableRow(getContext());
        final TableRow newRow2 = new TableRow(getContext());

        // Row 1
        EditText newInquiryName = new EditText(getContext());
        newInquiryName.setHint("Inquiry Name");

        Button newDateButton = new Button(getContext());
        newDateButton.setText("Select Date");
        newDateButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newDateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Row 2
        Spinner newSpinner = new Spinner(getContext());
        // Assuming you have a array for your spinner options, replace 'R.array.your_array' with your array's id
        ArrayAdapter<CharSequence> inquiriesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.inquiries_dispute_options, android.R.layout.simple_spinner_item);
        inquiriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(inquiriesAdapter);

        Button newAddButton7 = new Button(getContext());
        newAddButton7.setText("+");
        newAddButton7.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton7.setBackgroundResource(R.drawable.rounded_button);

        Button newRemoveButton7 = new Button(getContext());
        newRemoveButton7.setText("-");
        newRemoveButton7.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton7.setBackgroundResource(R.drawable.rounded_button);

        // Set layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams spinnerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 8f);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels

        newInquiryName.setLayoutParams(params1);
        newDateButton.setLayoutParams(params1);
        newSpinner.setLayoutParams(spinnerParams);
        newAddButton7.setLayoutParams(buttonParams);
        newRemoveButton7.setLayoutParams(buttonParams);

        newRow1.addView(newInquiryName);
        newRow1.addView(newDateButton);

        newRow2.addView(newSpinner);
        newRow2.addView(newAddButton7);
        newRow2.addView(newRemoveButton7);

        // Add the TableRows to the TableLayout
        inquiriesFormTable.addView(newRow1);
        inquiriesFormTable.addView(newRow2);

        // Set onClickListener for the new + button
        newAddButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows7();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the added rows
                inquiriesFormTable.removeView(newRow1);
                inquiriesFormTable.removeView(newRow2);
            }
        });
    }

    private void createNewRows8() {
        // Create new TableRows and Views
        final TableRow newRow1 = new TableRow(getContext());
        final TableRow newRow2 = new TableRow(getContext());

        // Row 1
        EditText newRecordName = new EditText(getContext());
        newRecordName.setHint("Record Name");

        Button newDateButton = new Button(getContext());
        newDateButton.setText("Select Date");
        newDateButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newDateButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Row 2
        Spinner newSpinner = new Spinner(getContext());
        // Assuming you have a array for your spinner options, replace 'R.array.your_array' with your array's id
        ArrayAdapter<CharSequence> recordsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.records_dispute_options, android.R.layout.simple_spinner_item);
        recordsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newSpinner.setAdapter(recordsAdapter);

        Button newAddButton8 = new Button(getContext());
        newAddButton8.setText("+");
        newAddButton8.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton8.setBackgroundResource(R.drawable.rounded_button);

        Button newRemoveButton8 = new Button(getContext());
        newRemoveButton8.setText("-");
        newRemoveButton8.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton8.setBackgroundResource(R.drawable.rounded_button);

        // Set layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow.LayoutParams spinnerParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels

        newRecordName.setLayoutParams(params1);
        newDateButton.setLayoutParams(params1);
        newSpinner.setLayoutParams(spinnerParams);
        newAddButton8.setLayoutParams(buttonParams);
        newRemoveButton8.setLayoutParams(buttonParams);

        newRow1.addView(newRecordName);
        newRow1.addView(newDateButton);

        newRow2.addView(newSpinner);
        newRow2.addView(newAddButton8);
        newRow2.addView(newRemoveButton8);

        // Add the TableRows to the TableLayout
        recordsFormTable.addView(newRow1);
        recordsFormTable.addView(newRow2);

        // Set onClickListener for the new + button
        newAddButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows8();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordsFormTable.removeView(newRow1);
                recordsFormTable.removeView(newRow2);
            }
        });
    }

    private void createNewRows12() {
        // Create new TableRow and Views
        final TableRow newRow = new TableRow(getContext());

        // Row
        EditText newDocumentName = new EditText(getContext());
        newDocumentName.setHint("Enter name of document");

        Button newAddButton12 = new Button(getContext());
        newAddButton12.setText("+");
        newAddButton12.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newAddButton12.setBackgroundResource(R.drawable.rounded_button);

        Button newRemoveButton12 = new Button(getContext());
        newRemoveButton12.setText("-");
        newRemoveButton12.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        newRemoveButton12.setBackgroundResource(R.drawable.rounded_button);

        // Set layout parameters for the Views
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f);
        TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        buttonParams.setMargins(5, 5, 5, 5);  // replace 5 with the desired space in pixels

        newDocumentName.setLayoutParams(params1);
        newAddButton12.setLayoutParams(buttonParams);
        newRemoveButton12.setLayoutParams(buttonParams);

        newRow.addView(newDocumentName);
        newRow.addView(newAddButton12);
        newRow.addView(newRemoveButton12);

        // Add the TableRow to the TableLayout
        documentsFormTable.addView(newRow);

        // Set onClickListener for the new + button
        newAddButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewRows12();
            }
        });

        // Set onClickListener for the new - button
        newRemoveButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentsFormTable.removeView(newRow);
            }
        });
    }

    private String generateRandomLetter() {
        String firstName = capitalizeFirstLetters(firstNameEditText.getText().toString());
        String lastName = capitalizeFirstLetters(lastNameEditText.getText().toString());
        String address = addressEditText.getText().toString();
        String apartment = apartmentEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = stateEditText.getText().toString();
        String zipCode = zipEditText.getText().toString();
        String socialLastFour = socialEditText.getText().toString();
        String birthDateString = datePickerButton4.getText().toString();
        String dateString = datePickerButton1.getText().toString();
        String section2 = section2Spinner.getSelectedItem().toString();

        String name = firstName + " " + lastName;
        String addressLine = address + (apartment.isEmpty() ? "" : ", " + apartment);
        String cityStateZip = city + ", " + state + " " + zipCode;

        List<List<Section>> letterSections = createLetterSections(name, addressLine, cityStateZip
                , socialLastFour, birthDateString, dateString, section2);
        Random random = new Random();
        StringBuilder randomLetter = new StringBuilder();

        for (int i = 0; i < NUM_SECTIONS; i++) {
            if (i < 3 || shouldIncludeSection(i) || i > 9) {
                int randomLetterIndex = random.nextInt(letterSections.size());
                randomLetter.append(letterSections.get(randomLetterIndex).get(i)).append("\n");
            }
        }

        return randomLetter.toString();
    }

    private List<List<Section>> createLetterSections(String name, String address,
                                                     String cityStateZip,
                                                     String socialLastFour, String birthDateString, String dateString,
                                                     String section2) {
        List<List<Section>> allLetterSections = new ArrayList<>();

        // Letter 1 sections
        List<Section> letter1Sections = new ArrayList<>();
        //Section 1
        letter1Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
        //Section 2
        letter1Sections.add(new Section(section2));
        //Section 3
        letter1Sections.add(new Section("I got a credit report, but it has mistakes. I need your help to fix it right away, because some important rules say we should.\n"));
        //Section 4
        letter1Sections.add(new Section("I need you to fix the wrong names on my credit report. Please change them to the right names. Thank you!" +
                " This is because a rule called FCRA Section 611 says so.\n" +
                "\n" +
                "\n" + getForm4Data()));
        //Section 5
        letter1Sections.add(new Section("I need you to fix the wrong addresses on my credit report. Please change them to the right addresses. Thank you!\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "A rule called FCRA Section 623 says you must do this.\n"));
        //Section 6
        letter1Sections.add(new Section("I need you to fix the wrong accounts on my credit report. Please correct the accounts that are not right. Thank you!\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "A rule called FCRA Section 623 says you must do this.\n"));
        //Section 7
        letter1Sections.add(new Section("I need you to take out the wrong inquiries from my credit report. Please remove the inquiries that are not right. Thank you!\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "A rule called FCRA Section 623 says you must do this.\n"));
        //Section 8
        letter1Sections.add(new Section("I need you to take out the wrong public records from my credit report. Please remove the records that are not right. Thank you!\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "A rule called FCRA Section 623 says you must do this.\n"));
        //Section 9
        letter1Sections.add(new Section("Please stop others from seeing my credit report by following a rule called FCRA Section 605B. This will help keep my information safe.\n"));
        //Section 10
        letter1Sections.add(new Section("Please stop sending me special offers by following a rule called FCRA Section 604(e)(1)(A).\n"));
        //Section 11
        letter1Sections.add(new Section("Thanks for helping me quickly. Tell me if you need more information.\n" +
                "\n" +
                "Your friend,\n" +
                "\n" +
                "\n" +
                name + "\n"));
        //Section 12
        letter1Sections.add(new Section("I put some papers with this:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter1Sections);

// Letter 2 sections
        List<Section> letter2Sections = new ArrayList<>();
//Section 1
        letter2Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter2Sections.add(new Section(section2));
//Section 3
        letter2Sections.add(new Section("I looked at my credit report, and I found some mistakes in it. Please help me fix these errors quickly because the Fair Credit Reporting Act says we need to do this.\n"));
//Section 4
        letter2Sections.add(new Section("Can you please remove the incorrect name(s) from my credit report? The FCRA Section 611 says we need to do this.I'm asking you to correct the names on my credit report that are not accurate. They should be fixed to show the correct names. Please take care of this issue promptly.\n" +
                "\n" +
                "\n" + getForm4Data() +
                "\n" +
                "This request is in accordance with FCRA Section 611.\n"
        ));
//Section 5
        letter2Sections.add(new Section("I'm asking you to correct the addresses on my credit report that are not accurate. They should be fixed to show the correct addresses. Please take care of this issue promptly.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This is needed because of FCRA Section 623.\n" +
                "\n"
        ));
//Section 6
        letter2Sections.add(new Section("I'm asking you to fix the accounts on my credit report that are not accurate. They should be corrected because they are not correct. Please take care of this promptly.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This is needed because of FCRA Section 623.\n" +
                "\n"
        ));
//Section 7
        letter2Sections.add(new Section("I'm asking you to remove the inquiries from my credit report that are not accurate. They should be taken off because they are not correct. Please take care of this promptly.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This is needed because of FCRA Section 623.\n"
        ));
//Section 8
        letter2Sections.add(new Section("I'm asking you to remove the public records from my credit report that are not accurate. They should be taken off because they are not correct. Please take care of this promptly.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This is needed because of FCRA Section 623.\n" +
                "\n"
        ));
//Section 9
        letter2Sections.add(new Section("I ask you to put a freeze on my credit report, so no one can access it without my permission, as stated in FCRA Section 605B. This will stop others from looking at it and help protect me from identity theft and fraud.\n" +
                "\n"
        ));
//Section 10
        letter2Sections.add(new Section("I want you to remove my name from the list of people who" +
                " get offers in the mail without asking, as stated in FCRA Section 604(e)(1)(A). I don't want those offers.\n"
        ));
//Section 11
        letter2Sections.add(new Section("Thank you for your fast help. Please let me know if you need any more details from me.\n" +
                "\n" +
                "Sincerely,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter2Sections.add(new Section("Here are the papers I've attached:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter2Sections);

        // Letter 3 sections
        List<Section> letter3Sections = new ArrayList<>();
//Section 1
        letter3Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter3Sections.add(new Section(section2));
//Section 3
        letter3Sections.add(new Section("I recently reviewed my credit report and discovered inaccuracies. Please address these issues promptly, as the Fair Credit Reporting Act (FCRA) requires us to do so under Sections 611 and 623.\n"));
//Section 4
        letter3Sections.add(new Section("I kindly request that you address the issue of inaccurate names on my credit report. It's important that these names are corrected to reflect the accurate information. I appreciate your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm4Data()));
//Section 5
        letter3Sections.add(new Section("I kindly request that you address the issue of inaccurate addresses on my credit report. It's important that these addresses are corrected to reflect the accurate information. I appreciate your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This action is required under FCRA Section 623.\n"));
//Section 6
        letter3Sections.add(new Section("I kindly request that you address the issue of inaccurate accounts on my credit report. It's important that these accounts are fixed to reflect the accurate information. I appreciate your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This action is required under FCRA Section 623.\n"));
//Section 7
        letter3Sections.add(new Section("I kindly request that you address the issue of inaccurate inquiries on my credit report. It's important that these inquiries are removed as they are not accurate. I appreciate your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This action is required under FCRA Section 623.\n"));
//Section 8
        letter3Sections.add(new Section("I kindly request that you address the issue of inaccurate public records on my credit report. It's important that these records are removed as they are not accurate. I appreciate your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This action is required under FCRA Section 623.\n"));
//Section 9
        letter3Sections.add(new Section("I request that you freeze my credit report as allowed by FCRA Section 605B. This action will restrict access to my credit information, making it more difficult for unauthorized individuals to use my identity or engage in fraudulent activities.\n"));
//Section 10
        letter3Sections.add(new Section("I request that you opt me out of receiving pre-screened offers as authorized by FCRA Section 604(e)(1)(A). I don't want to receive unsolicited offers in the mail.\n"));
//Section 11
        letter3Sections.add(new Section("I appreciate your prompt attention to this issue. If you need any additional information, please don't hesitate to ask.\n" +
                "\n" +
                "Respectfully,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter3Sections.add(new Section("I have attached these documents for you:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter3Sections);

// Letter 4 sections
        List<Section> letter4Sections = new ArrayList<>();
//Section 1
        letter4Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter4Sections.add(new Section(section2));
//Section 3
        letter4Sections.add(new Section("I recently obtained and reviewed my credit report, finding several inaccuracies that need your immediate attention. Please rectify these issues in accordance with the Fair Credit Reporting Act (FCRA) Sections 611 and 623.\n"));
//Section 4
        letter4Sections.add(new Section("I request that you rectify the inaccuracies in the names listed on my credit report. It is crucial that the names are updated to accurately represent my personal information. I trust that you will handle this matter promptly.\n" +
                "\n" +
                "\n" + getForm4Data()));
//Section 5
        letter4Sections.add(new Section("I strongly request that you remove the following erroneous address(es) from my credit report:I request that you rectify the inaccuracies in the addresses listed on my credit report. It is crucial that the addresses are updated to accurately represent my personal information. I trust that you will handle this matter promptly.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This request complies with FCRA Section 623.\n"));
//Section 6
        letter4Sections.add(new Section("I request that you rectify the inaccuracies in the accounts listed on my credit report. It is crucial that these accounts are updated to accurately represent my financial information. I trust that you will handle this matter promptly.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This request complies with FCRA Section 623.\n"));
//Section 7
        letter4Sections.add(new Section("I request that you remove the inaccurate inquiries listed on my credit report. It is crucial that these inquiries are deleted as they do not reflect my actual credit activities. I trust that you will handle this matter promptly.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This request complies with FCRA Section 623.\n"));
//Section 8
        letter4Sections.add(new Section("I request that you remove the inaccurate public records listed on my credit report. It is crucial that these records are deleted as they do not reflect my actual legal history. I trust that you will handle this matter promptly.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This request complies with FCRA Section 623.\n"));
//Section 9
        letter4Sections.add(new Section("I formally request that you implement a credit report freeze in accordance with FCRA Section 605B. This measure will limit the access to my credit report, safeguarding it from unauthorized use and potential identity theft or fraud.\n"));
//Section 10
        letter4Sections.add(new Section("I formally request that you exercise my right to opt out of receiving pre-screened offers as provided by FCRA Section 604(e)(1)(A). It is my preference not to receive unsolicited offers through the mail.\n"));
//Section 11
        letter4Sections.add(new Section("Thank you for addressing this matter swiftly. If you require any further information, kindly let me know.\n" +
                "\n" +
                "Best regards,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter4Sections.add(new Section("Attached you'll find the following documents:" +
                "\n" + getForm12Data() +
                "\n"));
                allLetterSections.add(letter4Sections);

// Letter 5 sections
        List<Section> letter5Sections = new ArrayList<>();
//Section 1
        letter5Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter5Sections.add(new Section(section2));
//Section 3
        letter5Sections.add(new Section("Upon reviewing my recently obtained credit report, I have identified inaccuracies that necessitate prompt resolution. Please address these issues in compliance with Sections 611 and 623 of the Fair Credit Reporting Act (FCRA).\n"));
//Section 4
        letter5Sections.add(new Section("I insist on the necessary corrections to be made to the names on my credit report. It is vital that the names accurately represent my personal information. I trust that you will promptly address this matter.\n" +
                "\n" +
                "\n" + getForm4Data()));
//Section 5
        letter5Sections.add(new Section("I insist on the necessary corrections to be made to the addresses on my credit report. It is vital that the addresses accurately represent my personal information. I trust that you will promptly address this matter.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This action is mandated by FCRA Section 623.\n"));
//Section 6
        letter5Sections.add(new Section("I insist on the necessary corrections to be made to the accounts on my credit report. It is vital that these accounts are corrected to accurately reflect my financial history. I trust that you will promptly address this matter.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This action is mandated by FCRA Section 623.\n"));
//Section 7
        letter5Sections.add(new Section("I insist on the necessary removal of the inaccurate inquiries from my credit report. It is vital that these inquiries are taken off as they do not accurately represent my credit history. I trust that you will promptly address this matter.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This action is mandated by FCRA Section 623.\n"));
//Section 8
        letter5Sections.add(new Section("I insist on the necessary removal of the inaccurate public records from my credit report. It is vital that these records are taken off as they do not accurately represent my legal history. I trust that you will promptly address this matter.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This action is mandated by FCRA Section 623.\n"));
//Section 9
        letter5Sections.add(new Section("I insist on the implementation of a credit report freeze as authorized by FCRA Section 605B. This protective measure will restrict access to my credit information, reducing the risk of unauthorized usage and mitigating potential identity theft or fraudulent activities.\n"));
//Section 10
        letter5Sections.add(new Section("I insist on exercising my right to opt out of receiving pre-screened offers in accordance with FCRA Section 604(e)(1)(A). I do not wish to receive unsolicited offers by mail.\n"));
//Section 11
        letter5Sections.add(new Section("I'm grateful for your prompt attention to this matter. Please inform me if you need any additional information.\n" +
                "\n" +
                "Kind regards,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter5Sections.add(new Section("Please find the attached documents listed below:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter5Sections);

        // Letter 6 sections
        List<Section> letter6Sections = new ArrayList<>();
//Section 1
        letter6Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter6Sections.add(new Section(section2));
//Section 3
        letter6Sections.add(new Section("I have acquired and scrutinized my credit report, revealing discrepancies that demand expeditious attention. Kindly rectify these inaccuracies in adherence to the Fair Credit Reporting Act (FCRA) Sections 611 and 623.\n"));
//Section 4
        letter6Sections.add(new Section("I formally request that you rectify the inaccuracies pertaining to the names on my credit report. It is important that the names are updated to accurately reflect my personal information. I expect your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm4Data()));
//Section 5
        letter6Sections.add(new Section("I formally request that you rectify the inaccuracies pertaining to the addresses on my credit report. It is important that the addresses are updated to accurately reflect my personal information. I expect your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This demand is in compliance with FCRA Section 623.\n"));
//Section 6
        letter6Sections.add(new Section("I formally request that you rectify the inaccuracies pertaining to the accounts on my credit report. It is important that these accounts are fixed to accurately represent my financial information. I expect your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This demand is in compliance with FCRA Section 623.\n"));
//Section 7
        letter6Sections.add(new Section("I formally request that you rectify the inaccuracies pertaining to the inquiries on my credit report. It is important that these inquiries are removed as they do not accurately represent my credit activities. I expect your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This demand is in compliance with FCRA Section 623.\n"));
//Section 8
        letter6Sections.add(new Section("I formally request that you rectify the inaccuracies pertaining to the public records on my credit report. It is important that these records are removed as they do not accurately represent my legal history. I expect your prompt attention to this matter.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This demand is in compliance with FCRA Section 623.\n"));
//Section 9
        letter6Sections.add(new Section("I strongly urge you to enforce a credit report freeze in compliance with FCRA Section 605B. By implementing this safeguard, access to my credit report will be restricted, significantly reducing the chances of unauthorized use, identity theft, or fraudulent behavior.\n"));
//Section 10
        letter6Sections.add(new Section("I strongly urge you to honor my request to opt out of receiving pre-screened offers as stipulated by FCRA Section 604(e)(1)(A). It is my explicit preference to decline unsolicited offers through mail.\n"));
//Section 11
        letter6Sections.add(new Section("Thank you for your expedient attention to this issue. Should you require any further information, please do not hesitate to contact me.\n" +
                "\n" +
                "Respectfully,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter6Sections.add(new Section("Enclosed, please find the subsequent documents:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter6Sections);

        // Letter 7 sections
        List<Section> letter7Sections = new ArrayList<>();
//Section 1
        letter7Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter7Sections.add(new Section(section2));
//Section 3
        letter7Sections.add(new Section("In reference to my recently procured credit report, a thorough examination revealed inaccuracies that warrant immediate intervention. I kindly request that you address these discrepancies in accordance with the stipulations set forth in Sections 611 and 623 of the Fair Credit Reporting Act (FCRA).\n"));
//Section 4
        letter7Sections.add(new Section("I demand that you promptly rectify the inaccuracies in the names listed on my credit report. It is crucial that the names accurately represent my personal information. I trust that you will take the necessary steps to resolve this matter expeditiously.\n" +
                "\n" +
                "\n" + getForm4Data()));
//Section 5
        letter7Sections.add(new Section("I demand that you promptly rectify the inaccuracies in the addresses listed on my credit report. It is crucial that the addresses accurately represent my personal information. I trust that you will take the necessary steps to resolve this matter expeditiously.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This request is pursuant to the provisions of FCRA Section 623.\n"));
//Section 6
        letter7Sections.add(new Section("I demand that you promptly rectify the inaccuracies in the accounts listed on my credit report. It is crucial that these accounts are fixed to reflect my financial history accurately. I trust that you will take the necessary steps to resolve this matter expeditiously.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This request is pursuant to the provisions of FCRA Section 623.\n"));
//Section 7
        letter7Sections.add(new Section("I demand that you promptly remove the inaccuracies in the inquiries listed on my credit report. It is crucial that these inquiries are deleted as they do not reflect my credit history accurately. I trust that you will take the necessary steps to resolve this matter expeditiously.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This request is pursuant to the provisions of FCRA Section 623.\n"));
//Section 8
        letter7Sections.add(new Section("I demand that you promptly remove the inaccuracies in the public records listed on my credit report. It is crucial that these records are deleted as they do not reflect my legal history accurately. I trust that you will take the necessary steps to resolve this matter expeditiously.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This request is pursuant to the provisions of FCRA Section 623.\n"));
//Section 9
        letter7Sections.add(new Section("I demand the immediate initiation of a credit report freeze in accordance with FCRA Section 605B. This proactive step will effectively limit access to my credit information, ensuring enhanced protection against identity theft, fraud, and unauthorized use.\n"));
//Section 10
        letter7Sections.add(new Section("I demand that you promptly opt me out of receiving pre-screened offers in strict accordance with FCRA Section 604(e)(1)(A). I expressly choose not to receive unsolicited offers through mail.\n"));
//Section 11
        letter7Sections.add(new Section("I sincerely appreciate your prompt attention to this matter. If any further information is required, kindly do not hesitate to reach out.\n" +
                "\n" +
                "Yours faithfully,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter7Sections.add(new Section("Kindly find the ensuing documents attached herewith:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter7Sections);

        // Letter 8 sections
        List<Section> letter8Sections = new ArrayList<>();
//Section 1
        letter8Sections.add(new Section(name +
                "\n" + address +
                "\n" + cityStateZip +
                "\nLast 4 of SSN: " +  socialLastFour +
                "\nBirthdate: " + birthDateString  +
                "\n" +
                "\n" + dateString + "\n"));
//Section 2
        letter8Sections.add(new Section(section2));
//Section 3
        letter8Sections.add(new Section("I am writing to you in regards to my credit report, which I recently obtained and reviewed. I have found inaccuracies that require your immediate attention, in accordance with the Fair Credit Reporting Act (FCRA) Sections 611 and 623.\n"));
//Section 4
        letter8Sections.add(new Section("I formally request that you address the issue of incorrect names on my credit report. It is imperative that these inaccuracies are rectified to ensure the accuracy and integrity of my personal information. I expect a prompt resolution to this matter in accordance with the laws governing credit reporting.\n" +
                "\n" +
                "\n" + getForm4Data() +
                "\n" +
                "This request is in accordance with FCRA Section 611.\n"));
//Section 5
        letter8Sections.add(new Section("I formally request that you address the issue of incorrect addresses on my credit report. It is imperative that these inaccuracies are rectified to ensure the accuracy and integrity of my personal information. I expect a prompt resolution to this matter in accordance with the laws governing credit reporting.\n" +
                "\n" +
                "\n" + getForm5Data() +
                "\n" +
                "This request is in accordance with FCRA Section 623.\n"));
//Section 6
        letter8Sections.add(new Section("I formally request that you address the issue of incorrect accounts on my credit report. It is imperative that these inaccuracies are rectified by fixing the inaccurate accounts. I expect a prompt resolution to this matter in accordance with the laws governing credit reporting.\n" +
                "\n" +
                "\n" + getForm6Data() +
                "\n" +
                "This request is in accordance with FCRA Section 623.\n"));
//Section 7
        letter8Sections.add(new Section("I formally request that you address the issue of incorrect inquiries on my credit report. It is imperative that these inaccuracies are rectified by removing the inaccurate inquiries. I expect a prompt resolution to this matter in accordance with the laws governing credit reporting.\n" +
                "\n" +
                "\n" + getForm7Data() +
                "\n" +
                "This request is in accordance with FCRA Section 623.\n"));
//Section 8
        letter8Sections.add(new Section("I formally request that you address the issue of incorrect public records on my credit report. It is imperative that these inaccuracies are rectified by removing the inaccurate records. I expect a prompt resolution to this matter in accordance with the laws governing credit reporting.\n" +
                "\n" +
                "\n" + getForm8Data() +
                "\n" +
                "This request is in accordance with FCRA Section 623.\n"));
//Section 9
        letter8Sections.add(new Section("I formally request the implementation of a credit report freeze as stipulated by FCRA Section 605B. This measure will securely restrict access to my credit report, providing a robust safeguard against potential identity theft, fraud, and unauthorized usage.\n"));
//Section 10
        letter8Sections.add(new Section("I formally request that you comply with my right to opt out of receiving pre-screened offers as mandated by FCRA Section 604(e)(1)(A). It is essential that you honor my preference to decline unsolicited offers sent through mail.\n"));
//Section 11
        letter8Sections.add(new Section("Thank you for your prompt attention to this matter. Please let me know if you require any further information from me.\n" +
                "\n" +
                "Sincerely,\n" +
                "\n" +
                "\n" +
                name + "\n" +
                "\n"
        ));
//Section 12
        letter8Sections.add(new Section("Attached please find the following documents:" +
                "\n" + getForm12Data() +
                "\n"));
        allLetterSections.add(letter8Sections);

        return allLetterSections;
    }

    private String capitalizeFirstLetters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private boolean shouldIncludeSection(int sectionIndex) {
        switch (sectionIndex) {
            case 3:
                return section4RadioGroup.getCheckedRadioButtonId() == R.id.radioButton4Yes;
            case 4:
                return section5RadioGroup.getCheckedRadioButtonId() == R.id.radioButton5Yes;
            case 5:
                return section6RadioGroup.getCheckedRadioButtonId() == R.id.radioButton6Yes;
            case 6:
                return section7RadioGroup.getCheckedRadioButtonId() == R.id.radioButton7Yes;
            case 7:
                return section8RadioGroup.getCheckedRadioButtonId() == R.id.radioButton8Yes;
            case 8:
                return section9RadioGroup.getCheckedRadioButtonId() == R.id.radioButton9Yes;
            case 9:
                return section10RadioGroup.getCheckedRadioButtonId() == R.id.radioButton10Yes;
            default:
                return false;
        }
    }

    private String getForm4Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = nameFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 2) {  // Skip by two because our form is on two rows
            TableRow row1 = (TableRow) nameFormTable.getChildAt(i);
            TableRow row2 = (TableRow) nameFormTable.getChildAt(i + 1);

            EditText name = (EditText) row1.getChildAt(0);
            Spinner reasonSpinner = (Spinner) row2.getChildAt(0);

            String nameText = name.getText().toString().trim();

            // Check if Spinner has a selected item before calling toString()
            String reasonText = (reasonSpinner.getSelectedItem() != null) ? reasonSpinner.getSelectedItem().toString() : "Default Reason";

            // Check if Spinner is on option 0 and show error message
            if (reasonSpinner.getSelectedItemPosition() == 0 && section4RadioGroup.getCheckedRadioButtonId() == R.id.radioButton4Yes) {
                Toast.makeText(getContext(), "Please choose a selection", Toast.LENGTH_SHORT).show();
            }

            // Check if nameText is not empty or spinner is not on the first option
            if ((!nameText.isEmpty() && reasonSpinner.getSelectedItemPosition() != 0) || reasonSpinner.getSelectedItemPosition() == 1) {
                formData.append("Name: ").append(nameText).append("\n");
                formData.append("Reason: ").append(reasonText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private String getForm5Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = addressFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 3) {  // Skip by three because our form is on three rows
            TableRow row1 = (TableRow) addressFormTable.getChildAt(i);
            TableRow row2 = (TableRow) addressFormTable.getChildAt(i + 1);
            TableRow row3 = (TableRow) addressFormTable.getChildAt(i + 2);

            EditText address = (EditText) row1.getChildAt(0);
            EditText apartment = (EditText) row1.getChildAt(1);
            EditText city = (EditText) row2.getChildAt(0);
            EditText state = (EditText) row2.getChildAt(1);
            EditText zipCode = (EditText) row2.getChildAt(2);
            Spinner reasonSpinner = (Spinner) row3.getChildAt(0);

            String addressText = address.getText().toString().trim();
            String apartmentText = apartment.getText().toString().trim();
            String cityText = city.getText().toString().trim();
            String stateText = state.getText().toString().trim();
            String zipCodeText = zipCode.getText().toString().trim();

            // Check if Spinner has a selected item before calling toString()
            String reasonText = (reasonSpinner.getSelectedItem() != null) ? reasonSpinner.getSelectedItem().toString() : "Default Reason";

            // Check if Spinner is on option 0 and show error message
            if (reasonSpinner.getSelectedItemPosition() == 0 && section5RadioGroup.getCheckedRadioButtonId() == R.id.radioButton5Yes) {
                Toast.makeText(getContext(), "Please choose a selection", Toast.LENGTH_SHORT).show();
            }

            // Check if all fields are not empty and spinner is not on the first option
            if ((!addressText.isEmpty() && !apartmentText.isEmpty() && !cityText.isEmpty()
                    && !stateText.isEmpty() && !zipCodeText.isEmpty() && reasonSpinner.getSelectedItemPosition() != 0)
                    || reasonSpinner.getSelectedItemPosition() == 1) {
                formData.append("Address: ").append(addressText).append("\n");
                formData.append("Apartment: ").append(apartmentText).append("\n");
                formData.append("City: ").append(cityText).append("\n");
                formData.append("State: ").append(stateText).append("\n");
                formData.append("Zip Code: ").append(zipCodeText).append("\n");
                formData.append("Reason: ").append(reasonText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private String getForm6Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = nameFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 2) {  // Skip by two because our form is on two rows
            TableRow row1 = (TableRow) accountsFormTable.getChildAt(i);
            TableRow row2 = (TableRow) accountsFormTable.getChildAt(i + 1);

            EditText accountName = (EditText) row1.getChildAt(0);
            EditText accountNumber = (EditText) row1.getChildAt(1);
            Spinner reasonSpinner = (Spinner) row2.getChildAt(0);
            // Button addButton = (Button) row2.getChildAt(1); // You can access the button here if needed.

            String accountNameText = accountName.getText().toString().trim();
            String accountNumberText = accountNumber.getText().toString().trim();

            // Check if Spinner has a selected item before calling toString()
            String reasonText = (reasonSpinner.getSelectedItem() != null) ? reasonSpinner.getSelectedItem().toString() : "Default Reason";

            // Check if Spinner is on option 0 and show error message
            if (reasonSpinner.getSelectedItemPosition() == 0 && section6RadioGroup.getCheckedRadioButtonId() == R.id.radioButton6Yes) {
                Toast.makeText(getContext(), "Please choose a selection", Toast.LENGTH_SHORT).show();
            }

            // Check if all fields are not empty and spinner is not on the first option
            if ((!accountNameText.isEmpty() && !accountNumberText.isEmpty() && reasonSpinner.getSelectedItemPosition() != 0)
                    || reasonSpinner.getSelectedItemPosition() == 1) {
                formData.append("Account Name: ").append(accountNameText).append("\n");
                formData.append("Account Number: ").append(accountNumberText).append("\n");
                formData.append("Reason: ").append(reasonText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private String getForm7Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = inquiriesFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 2) {  // Skip by two because our form is on two rows
            TableRow row1 = (TableRow) inquiriesFormTable.getChildAt(i);
            TableRow row2 = (TableRow) inquiriesFormTable.getChildAt(i + 1);

            EditText inquiryName = (EditText) row1.getChildAt(0);
            Button datePickerButton = (Button) row1.getChildAt(1);  // Get the Button from row1
            Spinner reasonSpinner = (Spinner) row2.getChildAt(0);

            String inquiryNameText = inquiryName.getText().toString().trim();
            String dateText = datePickerButton.getText().toString().trim();

            // Check if Spinner has a selected item before calling toString()
            String reasonText = (reasonSpinner.getSelectedItem() != null) ? reasonSpinner.getSelectedItem().toString() : "Default Reason";

            // Check if Spinner is on option 0 and show error message
            if (reasonSpinner.getSelectedItemPosition() == 0 && section7RadioGroup.getCheckedRadioButtonId() == R.id.radioButton7Yes) {
                Toast.makeText(getContext(), "Please choose a selection", Toast.LENGTH_SHORT).show();
            }

            // Check if nameText is not empty, dateText is not the default value, and spinner is not on the first option
            if ((!inquiryNameText.isEmpty() && !dateText.equals("Select Date") && reasonSpinner.getSelectedItemPosition() != 0)
                    || reasonSpinner.getSelectedItemPosition() == 1) {
                formData.append("Inquiry Name: ").append(inquiryNameText).append("\n");
                formData.append("Date: ").append(dateText).append("\n");
                formData.append("Reason: ").append(reasonText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private String getForm8Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = recordsFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 2) {  // Skip by two because our form is on two rows
            TableRow row1 = (TableRow) recordsFormTable.getChildAt(i);
            TableRow row2 = (TableRow) recordsFormTable.getChildAt(i + 1);

            EditText inquiryName = (EditText) row1.getChildAt(0);
            Button datePickerButton = (Button) row1.getChildAt(1);  // Get the Button from row1
            Spinner reasonSpinner = (Spinner) row2.getChildAt(0);

            String inquiryNameText = inquiryName.getText().toString().trim();
            String dateText = datePickerButton.getText().toString().trim();

            // Check if Spinner has a selected item before calling toString()
            String reasonText = (reasonSpinner.getSelectedItem() != null) ? reasonSpinner.getSelectedItem().toString() : "Default Reason";

            // Check if Spinner is on option 0 and show error message
            if (reasonSpinner.getSelectedItemPosition() == 0 && section8RadioGroup.getCheckedRadioButtonId() == R.id.radioButton8Yes) {
                Toast.makeText(getContext(), "Please choose a selection", Toast.LENGTH_SHORT).show();
            }

            // Check if nameText is not empty, dateText is not the default value, and spinner is not on the first option
            if ((!inquiryNameText.isEmpty() && !dateText.equals("Select Date") && reasonSpinner.getSelectedItemPosition() != 0)
                    || reasonSpinner.getSelectedItemPosition() == 1) {
                formData.append("Inquiry Name: ").append(inquiryNameText).append("\n");
                formData.append("Date: ").append(dateText).append("\n");
                formData.append("Reason: ").append(reasonText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private String getForm12Data() {
        StringBuilder formData = new StringBuilder();
        int childCount = documentsFormTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i++) {
            TableRow row = (TableRow) documentsFormTable.getChildAt(i);

            EditText documentNameEditText = (EditText) row.getChildAt(0);

            String documentNameText = documentNameEditText.getText().toString().trim();

            // Check if documentNameText is not empty
            if (!documentNameText.isEmpty()) {
                formData.append("Document Name: ").append(documentNameText).append("\n\n");
            }
        }

        return formData.toString();
    }

    private void setupDatePickerButton(Button datePickerButton, boolean isBirthDate) {
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(datePickerButton, isBirthDate);
            }
        });
    }

    private void showDatePickerDialog(final Button datePickerButton, final boolean isBirthDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateText(selectedDate, datePickerButton);
                        if (isBirthDate) {
                            birthdateSelected = true;
                        } else {
                            letterdateSelected = true;
                        }
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void updateDateText(Calendar calendar, Button datePickerButton) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String dateString = dateFormat.format(calendar.getTime());
        datePickerButton.setText(dateString);
    }

    private boolean validateSpinner(Spinner spinner) {
        int selectedItemPosition = spinner.getSelectedItemPosition();
        return selectedItemPosition != 0;
    }

    private boolean isAnyRadioButtonYesSelected() {
        // Get all radio groups in a list
        List<RadioGroup> radioGroups = Arrays.asList(
                section4RadioGroup, section5RadioGroup, section6RadioGroup,
                section7RadioGroup, section8RadioGroup, section9RadioGroup, section10RadioGroup
        );

        // Get all 'Yes' radio button IDs in a list
        List<Integer> yesRadioIds = Arrays.asList(
                R.id.radioButton4Yes, R.id.radioButton5Yes, R.id.radioButton6Yes,
                R.id.radioButton7Yes, R.id.radioButton8Yes, R.id.radioButton9Yes, R.id.radioButton10Yes
        );

        // Iterate over all radio groups and check if 'Yes' is selected in any of them
        for (int i = 0; i < radioGroups.size(); i++) {
            if (radioGroups.get(i).getCheckedRadioButtonId() == yesRadioIds.get(i)) {
                return true;
            }
        }

        // If none of the 'Yes' radio buttons is selected, return false
        return false;
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (isEmpty(firstNameEditText)) {
            firstNameEditText.setError("First name is required");
            isValid = false;
        }

        if (isEmpty(lastNameEditText)) {
            lastNameEditText.setError("Last name is required");
            isValid = false;
        }

        if (isEmpty(addressEditText)) {
            addressEditText.setError("Address is required");
            isValid = false;
        }

        if (isEmpty(cityEditText)) {
            cityEditText.setError("City is required");
            isValid = false;
        }

        if (isEmpty(stateEditText)) {
            stateEditText.setError("State is required");
            isValid = false;
        } else if (stateEditText.getText().length() != 2) {
            stateEditText.setError("State must be 2 Letters");
            isValid = false;
        }

        if (isEmpty(zipEditText)) {
            zipEditText.setError("Zip is required");
            isValid = false;
        } else if (zipEditText.getText().length() != 5) {
            zipEditText.setError("Zip code must be 5 digits");
            isValid = false;
        }

        if (isEmpty(socialEditText)) {
            socialEditText.setError("Last 4 of Social Security Number is required");
            isValid = false;
        } else if (socialEditText.getText().length() != 4) {
            socialEditText.setError("Last 4 of Social must be 4 digits");
            isValid = false;
        }

        // Spinner validation
        if (!validateSpinner(section2Spinner)) {
            TextView spinnerError = (TextView) section2Spinner.getSelectedView();
            spinnerError.setError("Please select an option");
            isValid = false;
        }

        if (!isAnyRadioButtonYesSelected()) {
            Toast.makeText(getContext(), "You must include, at least, one section",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate each form table
        isValid = isValid && validateFormTable(nameFormTable);
        //isValid = isValid && validateFormTable(addressFormTable);
        //isValid = isValid && validateFormTable(accountsFormTable);
        //isValid = isValid && validateFormTable(inquiriesFormTable);
        //isValid = isValid && validateFormTable(recordsFormTable);

        return isValid;
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    // Define a callback interface
    public interface UnsavedChangesCallback {
        void onUnsavedChanges(boolean hasUnsavedChanges);
    }

    private void doPrint(String letterContent) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter(getActivity(), letterContent), null);
    }

    private class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        private final String letterContent; // Add this line
        private int totalPages;
        private PrintAttributes currentAttributes;

        public MyPrintDocumentAdapter(Context context, String letterContent) {
            this.context = context;
            this.letterContent = letterContent; // Add this line
        }

        private PageRange[] computeWrittenPages(int pageCount) {
            List<PageRange> writtenPages = new ArrayList<>();
            int startPage = -1;
            int endPage = -1;
            for (int i = 0; i < pageCount; i++) {
                if (writtenPagesArray.get(i, 0) == 1 && startPage < 0) {
                    startPage = i;
                } else if (writtenPagesArray.get(i, 0) == 0 && startPage >= 0) {
                    endPage = i - 1;
                    writtenPages.add(new PageRange(startPage, endPage));
                    startPage = -1;
                    endPage = -1;
                }
            }
            if (startPage >= 0) {
                endPage = pageCount - 1;
                writtenPages.add(new PageRange(startPage, endPage));
            }
            PageRange[] writtenPagesArray = new PageRange[writtenPages.size()];
            writtenPages.toArray(writtenPagesArray);
            return writtenPagesArray;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle metadata) {
            // Calculate the total number of pages based on the content and print attributes
            totalPages = computePageCount(newAttributes);

            // Check for cancellation
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // Save the print attributes for use in the onWrite() method
            currentAttributes = newAttributes;

            // Create a PrintDocumentInfo object to report the document information to the print system
            String jobName = context.getString(R.string.app_name) + " Document";
            PrintDocumentInfo info = new PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages)
                    .build();

            // Inform the print system of the document layout
            callback.onLayoutFinished(info, true);
        }

        private int computePageCount(PrintAttributes printAttributes) {
            int itemsPerPage = 40; // default item count for portrait mode

            PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
            if (!pageSize.isPortrait()) {
                // Adjust items per page if needed for landscape orientation
                itemsPerPage = 60;
            }

            // Split the letterContent into lines
            String[] lines = letterContent.split("\n");

            // Determine number of print items
            int printItemCount = lines.length;

            return (int) Math.ceil(printItemCount / (double) itemsPerPage);
        }

        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {
            // Calculate the total number of pages
            int totalPages = computePageCount(currentAttributes);

            // Initialize the written pages array
            writtenPagesArray = new SparseIntArray();

            // Check if the PDF document has been created
            if (pdfDocument == null) {
                // PDF document has not been created - create a new one
                pdfDocument = new PrintedPdfDocument(context, currentAttributes);
            }

            try {
                // Loop over all the pages
                for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                    // Create a new PageInfo object for the current page
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageIndex).create();

                    // Start a new page using the PageInfo object
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                    // Check for cancellation
                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        pdfDocument.close();
                        pdfDocument = null;
                        return;
                    }

                    // Draw the content for the current page
                    drawPage(page, letterContent, pageIndex);

                    // Finish the current page
                    pdfDocument.finishPage(page);

                    // Update the written pages array
                    writtenPagesArray.put(pageIndex, 1);
                }

                // Create an array of PageRange objects representing the written pages
                PageRange[] writtenPages = computeWrittenPages(totalPages);

                // Write PDF document to file
                pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));

                // Signal the print framework that the document is complete
                callback.onWriteFinished(writtenPages);
            } catch (IOException e) {
                // An error occurred while writing the PDF document
                callback.onWriteFailed(e.toString());
            } finally {
                // Close the PDF document
                pdfDocument.close();
                pdfDocument = null;
            }
        }

        /*private void drawPage(PdfDocument.Page page, String letterContent, int pageIndex) {
            Canvas canvas = page.getCanvas();

            int linesPerPage = 50; // This should match the value in computePageCount()
            String[] lines = letterContent.split("\n");
            int totalPages = (int) Math.ceil((double) lines.length / (double) linesPerPage);

            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(12);

            float marginLeft = 54;
            float marginTop = 72;
            float lineHeight = textPaint.getTextSize() * 1.5f;

            int lineStart = pageIndex * linesPerPage;
            int lineEnd = Math.min(lineStart + linesPerPage, lines.length);

            for (int i = lineStart; i < lineEnd; i++) {
                float x = marginLeft;
                float y = marginTop + ((i - lineStart) * lineHeight);
                canvas.drawText(lines[i], x, y, textPaint);
            }
        }*/
        private void drawPage(PdfDocument.Page page, String letterContent, int pageIndex) {
            Canvas canvas = page.getCanvas();

            int linesPerPage = 50; // This should match the value in computePageCount()
            String[] lines = letterContent.split("\n");

            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(12);

            float marginLeft = 54;
            float marginTop = 72;
            float lineHeight = textPaint.getTextSize() * 1.5f;
            float maxWidth = page.getCanvas().getWidth() - 2 * marginLeft;

            int lineCount = 0;
            for (int i = pageIndex * linesPerPage; i < lines.length && lineCount < linesPerPage; i++) {
                List<String> wrappedLines = wrapText(lines[i], maxWidth, textPaint);
                for (String wrappedLine : wrappedLines) {
                    float x = marginLeft;
                    float y = marginTop + (lineCount * lineHeight);
                    canvas.drawText(wrappedLine, x, y, textPaint);
                    lineCount++;
                }
            }
        }

        private List<String> wrapText(String text, float maxWidth, TextPaint textPaint) {
            List<String> lines = new ArrayList<>();
            if (textPaint.measureText(text) < maxWidth) {
                // If the input string's width is less than maxWidth, it fits as is
                lines.add(text);
            } else {
                // If the input string's width is larger than maxWidth, split it
                String[] words = text.split("\\s");
                String currentLine = words[0];
                for (int i = 1; i < words.length; i++) {
                    if (textPaint.measureText(currentLine + " " + words[i]) <= maxWidth) {
                        currentLine = currentLine + " " + words[i];
                    } else {
                        lines.add(currentLine);
                        currentLine = words[i];
                    }
                }
                if (currentLine.length() > 0) {
                    lines.add(currentLine);
                }
            }
            return lines;
        }

    }

    private boolean validateFormTable(TableLayout formTable) {
        boolean isValid = true;

        int childCount = formTable.getChildCount();

        // Loop through all rows in the TableLayout
        for (int i = 0; i < childCount; i += 2) {  // Skip by two because our form is on two rows
            if (formTable.getChildAt(i) instanceof TableRow && formTable.getChildAt(i + 1) instanceof TableRow) {
                TableRow row1 = (TableRow) formTable.getChildAt(i);
                TableRow row2 = (TableRow) formTable.getChildAt(i + 1);

                // Check if the child views in the TableRow are of the expected types before casting
                if (row1.getChildAt(0) instanceof EditText && row2.getChildAt(0) instanceof Spinner) {
                    EditText name = (EditText) row1.getChildAt(0);
                    Spinner reasonSpinner = (Spinner) row2.getChildAt(0);

                    String nameText = name.getText().toString().trim();

                    // Check if Spinner has a selected item before calling toString()
                    int spinnerPosition = reasonSpinner.getSelectedItemPosition();

                    // Check if Spinner is on option 0 or name field is empty
                    if ((spinnerPosition == 0 && !nameText.isEmpty()) || (spinnerPosition != 0 && nameText.isEmpty())) {
                        Toast.makeText(getContext(), "Please ensure all fields are properly filled for each row", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

}