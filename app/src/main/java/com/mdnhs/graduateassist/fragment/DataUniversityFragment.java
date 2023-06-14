package com.mdnhs.graduateassist.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.adapter.DataUniversityAdapter;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.response.UniversityRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataUniversityFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private int position;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private DataUniversityAdapter dataUniversityAdapter;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.data_university_fragment, container, false);

        assert getArguments() != null;
        String getId = getArguments().getString("id");
        position = getArguments().getInt("position");
        String getType = getArguments().getString("type");
        String getTitle = getArguments().getString("title");

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getTitle);
        }

        onClick = (position, type, id, title) -> {

            Method.category = title;

            DataListFragment dataListFragment = new DataListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("type", type);
            bundle.putString("title", title);
            bundle.putInt("position", position);
            dataListFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout_sub, dataListFragment, title).commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);
        Method.category = getTitle;

        progressBar = view.findViewById(R.id.progressBar_ml_fragment);
        recyclerView = view.findViewById(R.id.recyclerView_ml_fragment);

        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.scrollToPositionWithOffset(position, 20);
        recyclerView.setFocusable(false);

        if (method.isNetworkAvailable()) {
            language();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

        DataListFragment dataListFragment = new DataListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", getId);
        bundle.putString("type", getType);
        bundle.putString("title", getTitle);
        dataListFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frameLayout_sub, dataListFragment, getTitle).commitAllowingStateLoss();

        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener((new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DataListFragment dataListFragment = new DataListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", "");
                bundle.putString("type", "searchMovie");
                bundle.putString("title", query);
                dataListFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                        dataListFragment, query).addToBackStack(query).commitAllowingStateLoss();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        }));
    }

    private void language() {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("method_name", "get_language_list");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<UniversityRP> call = apiService.getLanguage(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<UniversityRP>() {
                @Override
                public void onResponse(@NotNull Call<UniversityRP> call, @NotNull Response<UniversityRP> response) {

                    if (getActivity() != null) {

                        try {

                            final UniversityRP universityRP = response.body();
                            assert universityRP != null;

                            if (universityRP.getStatus().equals("1")) {

                                if (universityRP.getLanguageLists().size() != 0) {
                                    dataUniversityAdapter = new DataUniversityAdapter(getActivity(), universityRP.getLanguageLists(), "language", onClick);
                                    recyclerView.setAdapter(dataUniversityAdapter);
                                    dataUniversityAdapter.select(position);
                                    recyclerView.setVerticalScrollbarPosition(position);
                                }

                            } else {
                                method.alertBox(universityRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<UniversityRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }
    }

}
