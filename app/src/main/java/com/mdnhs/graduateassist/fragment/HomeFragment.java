package com.mdnhs.graduateassist.fragment;

import android.os.Bundle;
import android.os.Handler;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.adapter.HomeUniversityAdapter;
import com.mdnhs.graduateassist.adapter.HomeLanguageMovieAdapter;
import com.mdnhs.graduateassist.adapter.HomeDataAdapter;
import com.mdnhs.graduateassist.adapter.SliderAdapter;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.response.HomeRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.EnchantedViewPager;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private SliderAdapter sliderAdapter;
    private EnchantedViewPager enchantedViewPager;
    private HomeUniversityAdapter homeUniversityAdapter;
    private HomeLanguageMovieAdapter homeLanguageMovieAdapter;
    private HomeDataAdapter latestAdapter, recentAdapter;
    private ConstraintLayout conMain, conNoData, conSlider, conLatest, conRecent;
    private RecyclerView recyclerViewLanguage, recyclerViewLatest, recyclerViewRecent, recyclerViewMovieLag;

    private Timer timer;
    private Runnable Update;
    private final long DELAY_MS = 500; //delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 3000;
    private final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.home));
        }

        onClick = (position, type, id, title) -> {

            switch (type) {
                case "language_home":
                    DataUniversityFragment dataUniversityFragment = new DataUniversityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putInt("position", position);
                    bundle.putString("type", type);
                    bundle.putString("title", title);
                    dataUniversityFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                            dataUniversityFragment, "cat*").addToBackStack("cat*").commitAllowingStateLoss();
                    break;
                case "other":
                    movieList(id, type, title);
                    break;
                default:
                    DataDetailFragment dataDetailFragment = new DataDetailFragment();
                    Bundle bundleMD = new Bundle();
                    bundleMD.putString("id", id);
                    bundleMD.putString("type", type);
                    bundleMD.putString("title", title);
                    bundleMD.putInt("position", position);
                    dataDetailFragment.setArguments(bundleMD);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                            dataDetailFragment, title).addToBackStack(title).commitAllowingStateLoss();
                    break;
            }
        };
        method = new Method(getActivity(), onClick);

        enchantedViewPager = view.findViewById(R.id.viewPager_home);
        progressBar = view.findViewById(R.id.progressbar_home);
        conNoData = view.findViewById(R.id.con_noDataFound);
        conMain = view.findViewById(R.id.con_main_home);
        conSlider = view.findViewById(R.id.con_slider_home);
        conLatest = view.findViewById(R.id.con_latest_home);
        conRecent = view.findViewById(R.id.con_recent_home);
        recyclerViewLanguage = view.findViewById(R.id.recyclerView_language_home);
        recyclerViewLatest = view.findViewById(R.id.recyclerView_latest_home);
        recyclerViewRecent = view.findViewById(R.id.recyclerView_recent_home);
        recyclerViewMovieLag = view.findViewById(R.id.recyclerView_movie_home);
        MaterialTextView textViewLatest = view.findViewById(R.id.textView_latestViewAll_home);
        MaterialTextView textViewRecent = view.findViewById(R.id.textView_recentViewAll_home);

        progressBar.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);
        conMain.setVisibility(View.GONE);

        recyclerViewLanguage.setHasFixedSize(true);
        RecyclerView.LayoutManager lmLanguage = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewLanguage.setLayoutManager(lmLanguage);
        recyclerViewLanguage.setFocusable(false);
        recyclerViewLanguage.setNestedScrollingEnabled(false);

        recyclerViewLatest.setHasFixedSize(true);
        RecyclerView.LayoutManager lmLatest = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewLatest.setLayoutManager(lmLatest);
        recyclerViewLatest.setFocusable(false);
        recyclerViewLatest.setNestedScrollingEnabled(false);

        recyclerViewRecent.setHasFixedSize(true);
        RecyclerView.LayoutManager lmRecent = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecent.setLayoutManager(lmRecent);
        recyclerViewRecent.setFocusable(false);
        recyclerViewRecent.setNestedScrollingEnabled(false);

        recyclerViewMovieLag.setHasFixedSize(true);
        RecyclerView.LayoutManager lmMovie = new LinearLayoutManager(getContext());
        recyclerViewMovieLag.setLayoutManager(lmMovie);
        recyclerViewMovieLag.setFocusable(false);
        recyclerViewMovieLag.setNestedScrollingEnabled(false);

        textViewLatest.setOnClickListener(v -> movieList("", "latestMovie", getResources().getString(R.string.latest)));

        textViewRecent.setOnClickListener(v -> movieList("", "recentViewMovie", getResources().getString(R.string.recent_movie)));

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                home(method.userId());
            } else {
                home("0");
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

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

    private void home(String userId) {

        if (getActivity() != null) {

            progressBar.setVisibility(View.VISIBLE);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("method_name", "get_home");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHome(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {

                    if (getActivity() != null) {
                        try {
                            final HomeRP homeRP = response.body();
                            assert homeRP != null;

                            if (homeRP.getStatus().equals("1")) {

                                if (homeRP.getSliderLists().size() != 0) {

                                    int columnWidth = method.getScreenWidth();
                                    enchantedViewPager.setLayoutParams(new ConstraintLayout.LayoutParams(columnWidth, columnWidth / 2));

                                    enchantedViewPager.useScale();
                                    enchantedViewPager.removeAlpha();

                                    sliderAdapter = new SliderAdapter(getActivity(), "slider", homeRP.getSliderLists(), onClick);
                                    enchantedViewPager.setAdapter(sliderAdapter);

                                    Update = () -> {
                                        if (enchantedViewPager.getCurrentItem() == (sliderAdapter.getCount() - 1)) {
                                            enchantedViewPager.setCurrentItem(0, true);
                                        } else {
                                            enchantedViewPager.setCurrentItem(enchantedViewPager.getCurrentItem() + 1, true);
                                        }
                                    };

                                    if (sliderAdapter.getCount() > 1) {
                                        timer = new Timer(); // This will create a new Thread
                                        timer.schedule(new TimerTask() { // task to be scheduled
                                            @Override
                                            public void run() {
                                                handler.post(Update);
                                            }
                                        }, DELAY_MS, PERIOD_MS);
                                    }

                                } else {
                                    conSlider.setVisibility(View.GONE);
                                }

                                if (homeRP.getLanguageLists().size() != 0) {
                                    homeUniversityAdapter = new HomeUniversityAdapter(getActivity(), homeRP.getLanguageLists(), "language_home", onClick);
                                    recyclerViewLanguage.setAdapter(homeUniversityAdapter);
                                }

                                if (homeRP.getLatestLists().size() != 0) {
                                    latestAdapter = new HomeDataAdapter(getActivity(), homeRP.getLatestLists(), "latest_home", onClick);
                                    recyclerViewLatest.setAdapter(latestAdapter);
                                } else {
                                    conLatest.setVisibility(View.GONE);
                                }

                                if (homeRP.getRecentViewLists().size() != 0) {
                                    recentAdapter = new HomeDataAdapter(getActivity(), homeRP.getRecentViewLists(), "recent_home", onClick);
                                    recyclerViewRecent.setAdapter(recentAdapter);
                                } else {
                                    conRecent.setVisibility(View.GONE);
                                }

                                if (homeRP.getHomeMovieLists().size() != 0) {
                                    homeLanguageMovieAdapter = new HomeLanguageMovieAdapter(getActivity(), homeRP.getHomeMovieLists(), "other", onClick);
                                    recyclerViewMovieLag.setAdapter(homeLanguageMovieAdapter);
                                }

                                conMain.setVisibility(View.VISIBLE);

                            } else if (homeRP.getStatus().equals("2")) {
                                method.suspend(homeRP.getMessage());
                            } else {
                                conNoData.setVisibility(View.VISIBLE);
                                method.alertBox(homeRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    conNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

    private void movieList(String id, String type, String title) {
        DataListFragment dataListFragment = new DataListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("type", type);
        bundle.putString("title", title);
        dataListFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout_main, dataListFragment, title).addToBackStack(title).commitAllowingStateLoss();
    }

}
