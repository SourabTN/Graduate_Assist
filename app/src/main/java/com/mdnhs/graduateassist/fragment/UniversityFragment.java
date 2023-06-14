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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.adapter.UniversityAdapter;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.UniversityList;
import com.mdnhs.graduateassist.response.UniversityRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.EndlessRecyclerViewScrollListener;
import com.mdnhs.graduateassist.util.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UniversityFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private UniversityAdapter universityAdapter;
    private List<UniversityList> universityLists;
    private ConstraintLayout conNoData;
    private Boolean isOver = false;
    private int paginationIndex = 1;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.university_fragment, container, false);

        universityLists = new ArrayList<>();

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.university));
        }

        onClick = (position, type, id, title) -> {

            DataUniversityFragment dataUniversityFragment = new DataUniversityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putInt("position", position);
            bundle.putString("type", type);
            bundle.putString("title", title);
            dataUniversityFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout_main, dataUniversityFragment, "cat*").addToBackStack("cat*").commitAllowingStateLoss();

        };
        method = new Method(getActivity(), onClick);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_language);
        conNoData = view.findViewById(R.id.con_noDataFound);
        recyclerView = view.findViewById(R.id.recyclerView_language);

        progressBar.setVisibility(View.GONE);
        conNoData.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (universityAdapter != null) {
                    if (universityAdapter.getItemViewType(position) == 1) {
                        return 1;
                    } else {
                        return 3;
                    }
                }
                return 3;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    universityAdapter.hideHeader();
                }
            }
        });

        callData();

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

    private void callData() {
        if (method.isNetworkAvailable()) {
            language();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void language() {

        if (getActivity() != null) {

            if (universityAdapter == null) {
                universityLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_language");
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

                                if (universityRP.getLanguageLists().size() == 0) {
                                    if (universityAdapter != null) {
                                        universityAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    universityLists.addAll(universityRP.getLanguageLists());
                                }

                                if (universityAdapter == null) {
                                    if (universityLists.size() == 0) {
                                        conNoData.setVisibility(View.VISIBLE);
                                    } else {
                                        universityAdapter = new UniversityAdapter(getActivity(), universityLists, "language", onClick);
                                        recyclerView.setAdapter(universityAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    universityAdapter.notifyDataSetChanged();
                                }

                            } else {
                                conNoData.setVisibility(View.VISIBLE);
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
