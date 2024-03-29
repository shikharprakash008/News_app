package com.example.popularnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.popularnews.api.ApiClient;
import com.example.popularnews.api.ApiInterface;
import com.example.popularnews.models.Article;
import com.example.popularnews.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY="81653103d8f5425a935184668e4ea0fb";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles =new ArrayList<>();
    private Adapter adapter;
    private String TAG=MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // swipeRefreshLayout=findViewById(R.id.swipe_refresh_layout);
       // swipeRefreshLayout.setOnRefreshListener(this);
        //swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);

        recyclerView=findViewById(R.id.recyclerView);
        layoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        LoadJson("");
    }
    public void LoadJson(final String keyword){
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country =Utils.getCountry();
        String language =Utils.getLanguage();
        Call<News> call;

        if(keyword.length() >0){
            call=apiInterface.getNewsSearch(keyword,language,"publishedAt",API_KEY);

        }else{
            call = apiInterface.getNews(country,API_KEY);
        }


        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle()!= null){
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    articles =response.body().getArticle();
                    adapter=new Adapter(articles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MainActivity.this, "NO RESULT",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager =(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =(SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem =menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News ....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>2){
                    LoadJson(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                LoadJson(newText);
                return false;
            }
        });
        searchMenuItem.getIcon().setVisible(false,false);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
