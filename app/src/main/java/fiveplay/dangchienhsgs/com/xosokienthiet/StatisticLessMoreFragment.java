package fiveplay.dangchienhsgs.com.xosokienthiet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fiveplay.dangchienhsgs.com.xosokienthiet.adapter.FourColumnArrayAdapter;
import fiveplay.dangchienhsgs.com.xosokienthiet.dialogs.alerterror.NetworkErrorDialog;
import fiveplay.dangchienhsgs.com.xosokienthiet.dialogs.numberpicker.MyDialogNumberPicker;
import fiveplay.dangchienhsgs.com.xosokienthiet.model.NumberCouple;
import fiveplay.dangchienhsgs.com.xosokienthiet.service.ServiceUtilities;
import fiveplay.dangchienhsgs.com.xosokienthiet.utils.URLBuilder;


public class StatisticLessMoreFragment extends Fragment implements Button.OnClickListener, MyDialogNumberPicker.NumberPickerListener {
    private final String TAG = "Statistic Less More Fragment";
    TextView textTitleMax;
    TextView textTitleMin;
    private Button buttonNorth;
    private Button buttonMiddle;
    private Button buttonSouth;

    private LinearLayout layoutGroupCompanies;
    private String[] choosingCompanies;
    private String[] choosingCompaniesID;
    private Spinner spinnerNumberPicker;
    private List<String> listSpinnerItems;
    private ArrayAdapter<String> mSpinnerAdapter;
    private int range;
    private String code = "mienbac";
    private List<NumberCouple> listSpecial;
    private List<NumberCouple> listLotto;

    private ListView listViewMax;
    private ListView listViewMin;


    private boolean isStarted=false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_statistic_less_more, container, false);

        initComponents(view);
        return view;
    }

    public void initComponents(View view) {
        buttonNorth = (Button) view.findViewById(R.id.button_area_north);
        buttonMiddle = (Button) view.findViewById(R.id.button_area_middle);
        buttonSouth = (Button) view.findViewById(R.id.button_area_south);

        buttonNorth.setOnClickListener(this);
        buttonMiddle.setOnClickListener(this);
        buttonSouth.setOnClickListener(this);

        listViewMax = (ListView) view.findViewById(R.id.list_max);
        listViewMin = (ListView) view.findViewById(R.id.list_min);

        textTitleMax = (TextView) view.findViewById(R.id.text_title_explain_max);
        textTitleMin = (TextView) view.findViewById(R.id.text_title_explain_min);

        layoutGroupCompanies = (LinearLayout) view.findViewById(R.id.layout_group_companies);

        spinnerNumberPicker = (Spinner) view.findViewById(R.id.spinner_pick_num_times);
        spinnerNumberPicker.setVisibility(View.INVISIBLE);

        listSpinnerItems = new ArrayList<String>();
        for (String str : Common.DEFAULT_NUM_TIMES) {
            listSpinnerItems.add(str);
        }


        spinnerNumberPicker.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.popup_lanthongke));
        mSpinnerAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.spinner_current_item,
                listSpinnerItems
        );


        mSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerNumberPicker.setAdapter(mSpinnerAdapter);

        range = Integer.parseInt(listSpinnerItems.get(0));

        spinnerNumberPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == listSpinnerItems.size() - 1) {
                    MyDialogNumberPicker dialogNumberPicker = new MyDialogNumberPicker();
                    dialogNumberPicker.setListener(StatisticLessMoreFragment.this);
                    dialogNumberPicker.show(getFragmentManager(), "Nothing");
                } else {
                    range = (Integer.parseInt(listSpinnerItems.get(i)));
                    if (isStarted){
                        loadResult(code);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        textTitleMin.setVisibility(View.INVISIBLE);
        textTitleMax.setVisibility(View.INVISIBLE);
        listViewMax.setVisibility(View.INVISIBLE);
        listViewMin.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_area_north:
                choosingCompanies = Common.COMPANIES_IN_NORTH;
                choosingCompaniesID = Common.COMPANIES_IN_NORTH_ID;
                break;
            case R.id.button_area_middle:
                choosingCompanies = Common.COMPANIES_IN_MIDDLE;
                choosingCompaniesID = Common.COMPANIES_IN_MIDDLE_ID;
                break;
            case R.id.button_area_south:
                choosingCompanies = Common.COMPANIES_IN_SOUTH;
                choosingCompaniesID = Common.COMPANIES_IN_SOUTH_ID;
                break;
        }

        spinnerNumberPicker.setVisibility(View.VISIBLE);

        layoutGroupCompanies.removeAllViews();

        for (int i = 0; i < choosingCompanies.length; i++) {

            String company = choosingCompanies[i];

            // Add button to the row
            View companyView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_text_company, null);
            TextView textCompany = (TextView) companyView.findViewById(R.id.text_company);
            textCompany.setTextColor(getResources().getColor(R.color.text_table_color));
            textCompany.setText(company);
            companyView.setTag(String.valueOf(i));

            // Set onClickListener for the Button
            companyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Reset title for the activity
                    for (int j = 0; j < layoutGroupCompanies.getChildCount(); j++) {
                        View temp = layoutGroupCompanies.getChildAt(j);
                        ((TextView) temp.findViewById(R.id.text_company)).setTextColor(getResources().getColor(R.color.text_table_color));
                    }

                    ((TextView) view.findViewById(R.id.text_company)).setTextColor(getResources().getColor(R.color.orange_color));

                    code = choosingCompaniesID[Integer.parseInt((String) view.getTag())];
                    String company = choosingCompanies[Integer.parseInt((String) view.getTag())];

                    getActivity().getActionBar().setTitle(company);


                    //
                    isStarted=true;
                    // load Result
                    loadResult(code);

                    EasyTracker.getInstance(getActivity()).send(MapBuilder.createEvent(
                            "Choose area " + code,
                            "company button",
                            "Thong ke it nhieu",
                            null).build());

                }
            });

            layoutGroupCompanies.addView(companyView);
        }
    }

    public void loadResult(String code) {
        new DownloadInfoTask(code, range).execute();
    }

    @Override
    public void onNumberPickerReturn(int value) {
        Log.d(TAG, listSpinnerItems.toString());
        listSpinnerItems.add(listSpinnerItems.size() - 1, value + "");

        mSpinnerAdapter.notifyDataSetChanged();
        spinnerNumberPicker.setSelection(listSpinnerItems.size() - 2);

        this.range = value;


        loadResult(code);
    }

    public void updateResult() {
        List<String> listSpecialIndexMax = new ArrayList<String>();
        List<String> listSpecialValueMax = new ArrayList<String>();
        List<String> listSpecialIndexMin = new ArrayList<String>();
        List<String> listSpecialValueMin = new ArrayList<String>();

        List<String> listLottoIndexMax = new ArrayList<String>();
        List<String> listLottoValueMax = new ArrayList<String>();
        List<String> listLottoIndexMin = new ArrayList<String>();
        List<String> listLottoValueMin = new ArrayList<String>();


        for (int i = 0; i < 10; i++) {
            NumberCouple specialMax = listSpecial.get(i);
            NumberCouple specialMin = listSpecial.get(listSpecial.size() - i - 1);

            listSpecialIndexMax.add(specialMax.getIndex() + "");
            listSpecialValueMax.add(specialMax.getValue() + "");

            listSpecialIndexMin.add(specialMin.getIndex() + "");
            listSpecialValueMin.add(specialMin.getValue() + "");

            NumberCouple lottoMax = listLotto.get(i);
            NumberCouple lottoMin = listLotto.get(listLotto.size() - i - 1);

            listLottoIndexMax.add(lottoMax.getIndex() + "");
            listLottoValueMax.add(lottoMax.getValue() + "");

            listLottoIndexMin.add(lottoMin.getIndex() + "");
            listLottoValueMin.add(lottoMin.getValue() + "");
        }

        Log.d(TAG, listLottoIndexMax.toString());
        Log.d(TAG, listLottoIndexMin.toString());
        Log.d(TAG, listSpecialIndexMax.toString());
        Log.d(TAG, listSpecialIndexMin.toString());

        listLottoIndexMax.add(0, "Loto");
        listLottoValueMax.add(0, "Xuất hiện");
        listSpecialIndexMax.add(0, "Đặc biệt");
        listSpecialValueMax.add(0, "Xuất hiện");

        listLottoIndexMin.add(0, "Loto");
        listLottoValueMin.add(0, "Xuất hiện");
        listSpecialIndexMin.add(0, "Đặc biệt");
        listSpecialValueMin.add(0, "Xuất hiện");

        // Update list view
        FourColumnArrayAdapter maxAdapter = new FourColumnArrayAdapter(
                getActivity(),
                R.layout.row_four_columns,
                R.id.text_first_column,
                listSpecialIndexMax,
                listSpecialValueMax,
                listLottoIndexMax,
                listLottoValueMax
        );

        FourColumnArrayAdapter minAdapter = new FourColumnArrayAdapter(
                getActivity(),
                R.layout.row_four_columns,
                R.id.text_first_column,
                listSpecialIndexMin,
                listSpecialValueMin,
                listLottoIndexMin,
                listLottoValueMin
        );

        listViewMax.setAdapter(maxAdapter);
        listViewMin.setAdapter(minAdapter);


        textTitleMin.setVisibility(View.VISIBLE);
        textTitleMax.setVisibility(View.VISIBLE);
        listViewMax.setVisibility(View.VISIBLE);
        listViewMin.setVisibility(View.VISIBLE);

        textTitleMax.setText("Các số vé về nhiều nhất trong "+range+" lần quay xổ số");
        textTitleMin.setText("Các số vé về ít" +
                " nhất trong "+range+" lần quay xổ số");
    }

    public List<NumberCouple> analyzeResult(JSONArray jsonArray) {

        try {

            List<NumberCouple> list = new ArrayList<NumberCouple>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject element = (JSONObject) jsonArray.get(i);

                NumberCouple numberCouple = new NumberCouple(
                        element.getInt("number"),
                        element.getInt("count")
                );

                if (numberCouple.getValue() > 0) {
                    list.add(numberCouple);
                }
            }


            // begin sort desc
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    // If value i <value j or value i=value j  but index i <index j
                    if (list.get(i).getValue() < list.get(j).getValue()
                            || ((list.get(i).getValue() == list.get(j).getValue()) && list.get(i).getIndex() < list.get(j).getIndex())
                            ) {
                        // swap two element
                        NumberCouple temp = list.get(i);
                        list.set(i, list.get(j));
                        list.set(j, temp);
                    }
                }
            }

            return list;
        } catch (JSONException e) {
            Log.d(TAG, "Exception in json in analyze Result");

            return null;
        }
    }

    private class DownloadInfoTask extends AsyncTask<Void, String, String> {
        private String code;
        private int range;

        private DownloadInfoTask(String code, int range) {
            this.code = code;
            this.range = range;
        }

        @Override
        protected String doInBackground(Void... voids) {
            URLBuilder urlBuilder = new URLBuilder(URLBuilder.URL_THONG_KE_IT_NHIEU);

            urlBuilder.append(Common.LOCATION_CODE, code);
            urlBuilder.append(Common.RANGE, String.valueOf(range));

            String url = urlBuilder.create();

            String result = ServiceUtilities.sendGet(url, null);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray dacbiet = jsonObject.getJSONArray("dacbiet");
                JSONArray loto = jsonObject.getJSONArray("loto");

                listLotto = analyzeResult(loto);
                listSpecial = analyzeResult(dacbiet);

                updateResult();

            } catch (JSONException e) {
                Log.d(TAG, "The json from server is error : " + s);
            } catch (NullPointerException e) {
                NetworkErrorDialog dialog = new NetworkErrorDialog();
                dialog.setTitle("Thông báo");
                dialog.setContent("Lỗi mạng hoặc lỗi server, ấn retry để kết nối lại !");
                dialog.setListener(new NetworkErrorDialog.OnRetryListener() {
                    @Override
                    public void onDialogRetry() {
                        new DownloadInfoTask(code, range).execute();
                    }

                    @Override
                    public void onDialogClose() {

                    }
                });

                dialog.show(StatisticLessMoreFragment.this.getFragmentManager(), "Error Network Dialog");
            }

        }
    }

}
