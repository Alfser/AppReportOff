package com.example.celulareport.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.celulareport.R;
import com.example.celulareport.ui.Constraint;
import com.example.celulareport.ui.adapter.CardAdapter;
import com.example.celulareport.viewmodel.ReportListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsListFragment extends Fragment implements CardAdapter.OnCardClickListener, CardAdapter.OnCardLongCLickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MONTH = "Month";

    private String mMonth;

    private RecyclerView mRecycler;

    private CardAdapter mAdapter;

    private Toolbar mToolbar;

    private TextView textTitle;

    private CardView mCardView;

    private ReportListViewModel mViewModel;

    // Get position item when long click is activate
    int longClickPosition;

    //Context menu mode
    private ActionMode mActionMode;

    public ReportsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectMonth month selected in list of month of report.
     * @return A new instance of fragment ReportsListFragment.
     */
    public static ReportsListFragment newInstance(String selectMonth) {
        ReportsListFragment fragment = new ReportsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MONTH, selectMonth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mMonth = getArguments().getString(ARG_MONTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reports_list, container, false);

        mRecycler = v.findViewById(R.id.rv_report_list);

        //Setup toolbar as actionbar
        SetupToolbar(mMonth, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Create view model to share between fragments
        mViewModel =  new ViewModelProvider(requireActivity()).get(ReportListViewModel.class);

        //Observe if there're changes in reports Card list
        subscribeReportList(mViewModel, mMonth);

        //initializing Recyclerview
        SetupRecycler();
    }

    //Setup toolbar and collapsing bar
    @SuppressLint("RestrictedApi")
    private void SetupToolbar(String title, View v){
        mToolbar = (Toolbar) v.findViewById(R.id.reports_toolbar);
        textTitle = (TextView)v.findViewById(R.id.month_title);
        textTitle.setText(title);
        mToolbar.setLogo(R.drawable.ic_logo_mir);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        //ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayHomeAsUpEnabled(true);

    }

    //Adding recycleView(list of reports)
    public void SetupRecycler(){

        final int nColumn = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), nColumn);
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new CardAdapter(getContext(), this, this);
        mRecycler.setAdapter(mAdapter);
        //divisor between cards
        //mRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        //mRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){
            case R.id.ic_reports_search:
                Toast.makeText(getContext(), "Click in Searching icon!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.ic_add_report:
                //Toast.makeText(getContext(), "Click in add report icon!", Toast.LENGTH_SHORT).show();
                AddReportFragment fragment = new AddReportFragment();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.enter_slide_up, R.anim.exit_fade_freeze, R.anim.enter_fade_freeze, R.anim.exit_slide_down)
                        .replace(R.id.main_container, fragment, fragment.getClass().getName())
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                return true;
            default:
                Toast.makeText(getContext(), "Icon no treated", Toast.LENGTH_SHORT).show();
                break;
        }

        return false;
    }

    // menu context when we have a long click in the card
    public ActionMode.Callback getActionModeCallBack(){


        ActionMode.Callback actionCallback= new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.card_context_menu, menu);
                mode.setTitle("opções");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()){
                    case R.id.ic_delete_card:
                        long id = mAdapter.getReportId(longClickPosition);
                        mViewModel.deleteById(id);
                        mode.finish();
                        return true;
                    case R.id.ic_share_card:
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        };

        return actionCallback;
    }


    @Override
    public void onCardClick(int position, CardView mCardView) {
        if(mActionMode != null){
            mActionMode.finish();
        }
        else{
            //Toast.makeText(getContext(), "Clicked item " + position, Toast.LENGTH_SHORT).show();
            long reportID;
            reportID = mAdapter.getReportId(position);

            ReportDetailsFragment fragment = ReportDetailsFragment.newInstance(reportID);
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.enter_fade_in,
                            R.anim.exit_fade_in,
                            R.anim.enter_fade_in,
                            R.anim.exit_fade_in)
                    .replace(R.id.main_container, fragment, fragment.getClass().getName())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    @Override
    public boolean onCardLongClick(int position, CardView mCardView) {
        //FIXME: Disable simple click when actionMode enable or disable actionMode when it happened

        if (mActionMode == null){

            //trigger menu context
            ActionMode.Callback mCallback = getActionModeCallBack();
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mCallback);
            this.mCardView = mCardView;
            mCardView.setSelected(true);
            longClickPosition = position;

        }else {
            mCardView.setSelected(false);
            mActionMode.finish();
        }


        return true;
    }

    //Verify the month to return number according to month to search in database
    private String returnMonthValue(String monthSelect){
        String monthValue = "";
        switch (monthSelect){
            case Constraint.STRING_JANUARY:
                return monthValue = Constraint.VALUE_JANUARY;

            case Constraint.STRING_FEBRUARY:
                return monthValue = Constraint.VALUE_FEBRUARY;

            case Constraint.STRING_MARCH:
                return monthValue = Constraint.VALUE_MARCH;

            case Constraint.STRING_APRIL:
                return monthValue = Constraint.VALUE_APRIL;

            case Constraint.STRING_MAY:
                return monthValue = Constraint.VALUE_MAY;

            case Constraint.STRING_JUNE:
                return monthValue = Constraint.VALUE_JUNE;

            case Constraint.STRING_JULY:
                return monthValue = Constraint.VALUE_JULY;

            case Constraint.STRING_AUGUST:
                return monthValue = Constraint.VALUE_AUGUST;

            case Constraint.STRING_SEPTEMBER:
                return monthValue = Constraint.VALUE_SEPTEMBER;

            case Constraint.STRING_OCTOBER:
                return monthValue = Constraint.VALUE_OCTOBER;

            case Constraint.STRING_NOVEMBER:
                return monthValue = Constraint.VALUE_NOVEMBER;

            case Constraint.STRING_DECEMBER:
                return monthValue = Constraint.VALUE_DECEMBER;

            default:
                throw new NullPointerException("Value don't exist!");
        }


    }

    //observe changes in report cards list
    public void subscribeReportList(ReportListViewModel viewModel, String monthSelect){

        try {

            //Verify month value to query by it
            String monthValue = returnMonthValue(monthSelect);
            //select reports by month and observe changes in that
            viewModel.getReportsByMonth(monthValue).observe(getViewLifecycleOwner(), reportCards -> {
                mAdapter.setReportCardsByMonth(reportCards);
            });

        } catch (NullPointerException exception){
            Toast.makeText(getContext(),exception.getMessage().toString(), Toast.LENGTH_LONG);
        }
    }

}