package com.monideepde.showmetheway;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    public static final String ADD_A_NEW_DESTINATION_STRING = "Add a new destination";
    private ArrayList<String> mDestinationList;
    View mRootView;
    LayoutInflater mInflater;
    ViewGroup mContainer;
    Bundle mSavedInstanceState;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Toast.makeText(getActivity().getApplicationContext(), "In onCreateView of Home Fragment", Toast.LENGTH_LONG).show();
        mRootView = inflater.inflate(R.layout.fragment_home, container, false);

        mInflater = inflater;
        mContainer = container;
        mSavedInstanceState = savedInstanceState;

        setUpSpinner();
        return mRootView;
    }

    protected void setUpSpinner() {
        Spinner spinner = (Spinner) mRootView.findViewById(R.id.destinationList_spinner);

        getAllDestinationList();
        String[] dropdown_destination = new String[mDestinationList.size()];
        //dropdown_destination = (String[]) mDestinationList.toArray();
        int i=0;
        for(String s : mDestinationList) {
            dropdown_destination[i]=s; i++;
        }


        ArrayAdapter<String> destinationSpinnerArrayAdapter =  new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, dropdown_destination);
        destinationSpinnerArrayAdapter.notifyDataSetChanged();
        destinationSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(destinationSpinnerArrayAdapter);



    }

    @Override
    public void onResume() {
        super.onResume();
        //onCreateView(mInflater, mContainer, mSavedInstanceState);
        setUpSpinner();
        //Toast.makeText(getActivity().getApplicationContext(), "In onResume of Home Fragment", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    protected void getAllDestinationList(){

        //Get DBTools instance
        Activity a = getActivity();
        Context c = a.getApplicationContext();
        DBTools dbTools = DBTools.getInstance(c);

        mDestinationList = dbTools.getAllDestinationNames();
        mDestinationList.add(ADD_A_NEW_DESTINATION_STRING);

    }

}


