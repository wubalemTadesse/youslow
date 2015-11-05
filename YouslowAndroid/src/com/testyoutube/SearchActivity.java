package com.testyoutube;

import com.squareup.picasso.Picasso;

import android.support.v7.app.ActionBarDrawerToggle; 
import android.support.v4.widget.DrawerLayout; 

public class SearchActivity extends Activity {
 
    private EditText searchInput;
    private ListView videosFound;
     
    private Handler handler;
    
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);         
        searchInput = (EditText)findViewById(R.id.search_input);
        videosFound = (ListView)findViewById(R.id.videos_found); 
         
        handler = new Handler();
         
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {           
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {           
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });
        
        addClickListener();
        
        // Setting drawer
        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.statistic_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);         
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Tie together the the proper interactions between drawer and action bar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); 
            }
            
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();  
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // Default search for grumpy cat
        searchOnYoutube("grumpy cat");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Called whenever invalidateOptionsMenu() called
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
            	// Switching to line chart
            	Intent intent = new LineChart().execute(getApplicationContext());
            	startActivity(intent);	
            }
            else{
            	// Switching to pie chart
            	Intent intent = new Intent(getApplicationContext(), PiechartActivity.class);
            	startActivity(intent);             
            }
        }
    }
  
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
  
    
    private List<VideoItem> searchResults;
    
    private void searchOnYoutube(final String keywords){
            new Thread(){
                public void run(){
                    YoutubeConnector connector = new YoutubeConnector(SearchActivity.this);
                    searchResults = connector.search(keywords);                
                    handler.post(new Runnable(){
                        public void run(){
                            updateVideosFound();
                        }
                    });
                }
            }.start();
        }

    // Putting search result to list of views
    private void updateVideosFound(){
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView description = (TextView)convertView.findViewById(R.id.video_description);
                 
                VideoItem searchResult = searchResults.get(position);
                 
                // Using Picasso API to build snapshot for each result
                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                title.setTextSize(18);
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };          
         
        videosFound.setAdapter(adapter);
    }
    
    // Switching to video player
    private void addClickListener(){
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {     
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                    long id) {              
            	Intent intent = new Intent(getApplicationContext(), WebplayerActivity.class);
            	intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
            	startActivity(intent);
            	
            }
             
        });
    }
    
}
