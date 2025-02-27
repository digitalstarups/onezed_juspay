package com.onezed.Fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.onezed.Activity.HomeActivity;
import com.onezed.Adapter.contact_adapter;
import com.onezed.R;
import com.onezed.databinding.FragmentMobileRechargeBinding;

import java.util.ArrayList;
import java.util.List;

public class MobileRechargeFragment extends Fragment {

    private FragmentMobileRechargeBinding binding;
    private static final int REQUEST_READ_CONTACTS = 101;
    String value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMobileRechargeBinding.inflate(inflater, container, false);

        //Check Contact Permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            //if contact permission not granted
            retrieveContacts();
        }
        if (getArguments() != null) {
            value = getArguments().getString("PAGE");
        }

        binding.manualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberKeyboard();
            }
        });

        binding.txtMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //binding.newNumberLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.newNumberLayout.setVisibility(View.GONE);

                // Check if adapter is null before accessing it
                contact_adapter adapter = (contact_adapter) binding.contactListView.getAdapter();
                if (adapter != null) {
                    adapter.getFilter().filter(charSequence);
                } else {
                    // Handle the case when adapter is null (e.g., no contacts available or permission denied)
                    binding.newNumberLayout.setVisibility(View.VISIBLE);
                    binding.newNumber.setText(charSequence.toString());
                    binding.newNumberLayout.setOnClickListener(view -> {
                        if (charSequence.length() == 10) {
                            Bundle bundle = new Bundle();
                            bundle.putString("name", "unknown");
                            bundle.putString("mobileNo", charSequence.toString().trim());

                            SelectRechargePlanFragment secondFragment = new SelectRechargePlanFragment();
                            secondFragment.setArguments(bundle);

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.recharge_frameLayout, secondFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            binding.txtMobileNo.setError("Invalid Mobile Number");
                            binding.txtMobileNo.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.error_edit_bg));
                        }
                    });
                    return;
                }

                // Check if the filtered results are empty
                binding.contactListView.post(() -> {
                    adapter.setOnFilterCompleteListener(isEmpty -> {
                        if (isEmpty) {
                            binding.newNumberLayout.setVisibility(View.VISIBLE);
                            binding.newNumber.setText(charSequence.toString());
                            binding.newNumberLayout.setOnClickListener(view -> {
                                if (charSequence.length() == 10) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", "unknown");
                                    bundle.putString("mobileNo", charSequence.toString());

                                    SelectRechargePlanFragment secondFragment = new SelectRechargePlanFragment();
                                    secondFragment.setArguments(bundle);

                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.recharge_frameLayout, secondFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    binding.txtMobileNo.setError("Invalid Mobile Number");
                                    binding.txtMobileNo.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.error_edit_bg));
                                }
                            });
                        } else {
                            binding.newNumberLayout.setVisibility(View.GONE);
                        }
                    });
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Set OnItemClickListener for ListView
        binding.contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the adapter
                contact_adapter adapter = (contact_adapter) parent.getAdapter();
                // Get the clicked item's name and phone number
                String clickedName = adapter.getName(position);
                String clickedPhone = adapter.getPhoneNumber(position);
                // Show Toast with the clicked item details
                // Toast.makeText(getContext(), "Clicked: " + clickedName + " (" + clickedPhone + ")", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("name", clickedName); // Replace "key_name" and "value_name" with your actual key and value
                bundle.putString("mobileNo", clickedPhone);

                // Create an instance of the second fragment
                SelectRechargePlanFragment secondFragment = new SelectRechargePlanFragment();
                // Set the arguments (the bundle) on the second fragment
                secondFragment.setArguments(bundle);

                // Replace the current fragment with the new one
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.recharge_frameLayout, secondFragment); // Use the container ID where the fragment should be placed
                transaction.addToBackStack(null); // Optional: add to back stack if you want to allow users to navigate back
                transaction.commit();
            }
        });

        //back press handle
        //binding.btnViewProfileBackImage.setOnClickListener(view -> startActivity(new Intent(getActivity(), HomeActivity.class)));
        binding.btnViewProfileBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if (value!=null) {
                        if (value.equals("BBPS")) {
                            BBPSFragment fm = new BBPSFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.recharge_frameLayout, fm);
                            transaction.addToBackStack(null); // Optional: Add to back stack
                            transaction.commit();
                        } else {
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                        }
                    }
                    else {
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }

            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               // startActivity(new Intent(getActivity(), HomeActivity.class));
                if (value!=null) {
                    if (value.equals("BBPS")) {
                        BBPSFragment fm = new BBPSFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.recharge_frameLayout, fm);
                        transaction.addToBackStack(null); // Optional: Add to back stack
                        transaction.commit();
                    } else {
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }
                }
                else {
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                }

            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);


        return binding.getRoot();
    }

    private void showNumberKeyboard() {
        binding.txtMobileNo.requestFocus();
        binding.txtMobileNo.setInputType(InputType.TYPE_CLASS_PHONE);


        // Get the InputMethodManager
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(binding.txtMobileNo, InputMethodManager.SHOW_IMPLICIT);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with reading contacts
                retrieveContacts();
            } else {
                // Permission denied
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    // If permission was denied but "Don't ask again" wasn't checked
                    Toast.makeText(getContext(), "Permission denied, you need to grant permission to access contacts.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                } else {

                }

                // Optionally, you can navigate back to HomeActivity or take other action
                //startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        }
    }

    private void retrieveContacts() {
        List<String> names = new ArrayList<>();
        List<String> numbers = new ArrayList<>();

        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                // Trim the phone number and remove unwanted characters
                if (number != null) {
                    number = number.trim(); // Remove leading and trailing spaces
                    number = number.replaceAll("[^0-9+]", ""); // Remove non-numeric characters except '+'
                }

                names.add(name);
                numbers.add(number);
            }
            cursor.close();
        }

        contact_adapter adapter = new contact_adapter(getActivity(), names, numbers);
        binding.contactListView.setAdapter(adapter);
    }






}