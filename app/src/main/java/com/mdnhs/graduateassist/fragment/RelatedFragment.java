package com.mdnhs.graduateassist.fragment;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.adapter.DataAdapter;
import com.mdnhs.graduateassist.interfaces.OnClick;
import com.mdnhs.graduateassist.item.DataList;
import com.mdnhs.graduateassist.response.DataListRP;
import com.mdnhs.graduateassist.rest.ApiClient;
import com.mdnhs.graduateassist.rest.ApiInterface;
import com.mdnhs.graduateassist.util.API;
import com.mdnhs.graduateassist.util.Constant;
import com.mdnhs.graduateassist.util.EndlessRecyclerViewScrollListener;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RelatedFragment extends Fragment {

    private Method method;
    private OnClick onClick;
    private String id, languageId;
    private ProgressBar progressBar;
    private List<DataList> dataLists;
    private DataAdapter dataAdapter;
    private RecyclerView recyclerView;
    private ConstraintLayout conNoData;
    private ImageView imageViewData;
    private MaterialButton buttonLogin;
    private MaterialTextView textViewData;
    private Boolean isOver = false;
    private int paginationIndex = 1, totalArraySize = 0;
    private LayoutAnimationController animation;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.data_list_fragment, container, false);

        id = getArguments().getString("id");
        languageId = getArguments().getString("languageId");

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.related_movie));
        }

        dataLists = new ArrayList<>();

        onClick = (position, type, id, title) -> {
            DataDetailFragment dataDetailFragment = new DataDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("type", type);
            bundle.putString("title", title);
            bundle.putInt("position", position);
            dataDetailFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_main,
                    dataDetailFragment, title).addToBackStack(title).commitAllowingStateLoss();
        };
        method = new Method(getActivity(), onClick);

        int resId = R.anim.layout_animation_fall_down;
        animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);

        progressBar = view.findViewById(R.id.progressbar_movieList);
        conNoData = view.findViewById(R.id.con_not_login);
        imageViewData = view.findViewById(R.id.imageView_not_login);
        buttonLogin = view.findViewById(R.id.button_not_login);
        textViewData = view.findViewById(R.id.textView_not_login);
        recyclerView = view.findViewById(R.id.recyclerView_movieList);

        data(false, false);
        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (dataAdapter.getItemViewType(position) == 2 || dataAdapter.isHeader(position)) ? layoutManager.getSpanCount() : 1;
            }
        });

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!isOver) {
                    new Handler().postDelayed(() -> {
                        paginationIndex++;
                        callData();
                    }, 1000);
                } else {
                    dataAdapter.hideHeader();
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private void data(boolean isShow, boolean isLogin) {
        if (isShow) {
            if (isLogin) {
                buttonLogin.setVisibility(View.VISIBLE);
                textViewData.setText(getResources().getString(R.string.you_have_not_login));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_login));
            } else {
                buttonLogin.setVisibility(View.GONE);
                textViewData.setText(getResources().getString(R.string.no_data_found));
                imageViewData.setImageDrawable(getResources().getDrawable(R.drawable.no_data));
            }
            conNoData.setVisibility(View.VISIBLE);
        } else {
            conNoData.setVisibility(View.GONE);
        }
    }

    private void callData() {
        if (method.isNetworkAvailable()) {
            movie();
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }
    }

    private void movie() {

        if (getActivity() != null) {

            if (dataAdapter == null) {
                dataLists.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("review_id", id);
            jsObj.addProperty("language_id", languageId);
            jsObj.addProperty("page", paginationIndex);
            jsObj.addProperty("method_name", "get_related_review");
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DataListRP> call = apiService.getMovieList(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<DataListRP>() {
                @Override
                public void onResponse(@NotNull Call<DataListRP> call, @NotNull Response<DataListRP> response) {

                    if (getActivity() != null) {

                        try {
                            final DataListRP dataListRP = response.body();
                            assert dataListRP != null;

                            if (dataListRP.getStatus().equals("1")) {

                                if (dataListRP.getMovieLists().size() == 0) {
                                    if (dataAdapter != null) {
                                        dataAdapter.hideHeader();
                                        isOver = true;
                                    }
                                } else {
                                    totalArraySize = totalArraySize + dataListRP.getMovieLists().size();
                                    for (int i = 0; i < dataListRP.getMovieLists().size(); i++) {
                                        dataLists.add(dataListRP.getMovieLists().get(i));

                                        if (Constant.appRP != null && Constant.nativeAdPos != 0 && Constant.appRP.isNative_ad()) {
                                            int abc = dataLists.lastIndexOf(null);
                                            if (((dataLists.size() - (abc + 1)) % Constant.nativeAdPos == 0) && (dataListRP.getMovieLists().size() - 1 != i || totalArraySize != 1000)) {
                                                dataLists.add(null);
                                            }
                                        }
                                    }
                                }

                                if (dataAdapter == null) {
                                    if (dataLists.size() == 0) {
                                        data(true, false);
                                    } else {
                                        dataAdapter = new DataAdapter(getActivity(), dataLists, "relatedMovie", onClick);
                                        recyclerView.setAdapter(dataAdapter);
                                        recyclerView.setLayoutAnimation(animation);
                                    }
                                } else {
                                    dataAdapter.notifyDataSetChanged();
                                }
                            } else {
                                data(true, false);
                                method.alertBox(dataListRP.getMessage());
                            }

                        } catch (Exception e) {
                            Log.d("exception_error", e.toString());
                            method.alertBox(getResources().getString(R.string.failed_try_again));
                        }

                    }

                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NotNull Call<DataListRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressBar.setVisibility(View.GONE);
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }
            });

        }

    }

}
