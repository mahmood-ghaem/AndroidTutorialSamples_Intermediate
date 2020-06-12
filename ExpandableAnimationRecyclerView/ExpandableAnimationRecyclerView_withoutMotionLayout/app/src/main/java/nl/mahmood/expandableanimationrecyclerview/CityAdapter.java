package nl.mahmood.expandableanimationrecyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    String TAG = "@@@@@CityAdapter:";
    Context mContext;
    List<City> mCityList;
    private boolean isLoading = false;
    private int visibleThreshold = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private RecyclerView recyclerView;

    public CityAdapter(Context context, List<City> cityList, final RecyclerView recyclerView)
    {
        this.mContext = context;
        this.mCityList = cityList;
        this.recyclerView = recyclerView;

        recyclerView.setItemAnimator(new RecyclerView.ItemAnimator()
        {
            @Override
            public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo)
            {
                return false;
            }

            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo)
            {
                return false;
            }

            @Override
            public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo)
            {
                return false;
            }

            @Override
            public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo)
            {
                Log.i(TAG, "animateChange:oldHolder: " + oldHolder);
                Log.i(TAG, "animateChange:newHolder: " + newHolder);
                ViewHolderCity viewHolderCity = (ViewHolderCity) newHolder;
                viewHolderCity.motionLayoutImage.setTransition(R.id.start, R.id.end);
                viewHolderCity.motionLayoutImage.transitionToEnd();
                viewHolderCity.motionLayoutText.setTransition(R.id.start, R.id.end);
                viewHolderCity.motionLayoutText.transitionToEnd();
                return false;
            }

            @Override
            public void runPendingAnimations()
            {
            }

            @Override
            public void endAnimation(@NonNull RecyclerView.ViewHolder item)
            {
                if (item instanceof ViewHolderCity)
                {
                    ViewHolderCity viewHolderCity = (ViewHolderCity) item;
                    City city = mCityList.get(viewHolderCity.getAdapterPosition());
                    city.setExpanded(false);
                }
            }

            @Override
            public void endAnimations()
            {
            }

            @Override
            public boolean isRunning()
            {
                return false;
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                int total = getTotal(recyclerView);
                int lastVisible = getLastVisiblePosition(recyclerView);

                if (!getLoading() && total <= lastVisible + visibleThreshold)
                {
                    if (onLoadMoreListener != null)
                        onLoadMoreListener.onLoadMore();
                    setLoading(true);
                    raiseAllRecyclerViewItems();
                }
            }
        });


    }

    private void raiseAllRecyclerViewItems()
    {
        City city;
        for (int i = 0; i < getTotal(recyclerView) - 1; i++)
        {
            city = mCityList.get(i);
            city.setExpanded(false);
        }
    }

    private int getTotal(RecyclerView recyclerView)
    {
        if (recyclerView != null)
        {
            final RecyclerView.LayoutManager layoutManager = recyclerView
                    .getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager)
            {
                return layoutManager.getItemCount();
            }
        }
        return 0;
    }

    public int getLastVisiblePosition(RecyclerView recyclerView)
    {
        if (recyclerView != null)
        {
            final RecyclerView.LayoutManager layoutManager = recyclerView
                    .getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager)
            {
                return ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            }
        }
        return 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener)
    {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public boolean getLoading()
    {
        return isLoading;
    }

    public void setLoading(boolean loading)
    {
        isLoading = loading;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mCityList.get(position) != null)
            return 1; //Item Data
        else
            return 2; //Item Loading
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (viewType == 1)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            return new ViewHolderCity(view);
        } else// if (viewType == 2)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_layout, parent, false);
            return new ViewHolderLoading(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof ViewHolderCity)
        {
            final ViewHolderCity viewHolderCity = (ViewHolderCity) holder;
            City city = mCityList.get(position);
            viewHolderCity.textViewTitle.setText(city.getTitle());
            viewHolderCity.textViewDescription.setText(city.getDescription());
            viewHolderCity.textViewPopulation.setText(city.getPopulation());
            viewHolderCity.textViewWebAddress.setText(city.getWebsite());

            String uriAvatar = "@drawable/" + mCityList.get(position).getAvatar();
            int imageResourceAvatar = mContext.getResources().getIdentifier(uriAvatar, null, mContext.getPackageName());
            viewHolderCity.imageViewAvatar.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), imageResourceAvatar, null));

            String uriDescription = "@drawable/" + mCityList.get(position).getImage();
            int imageResourceDescription1 = mContext.getResources().getIdentifier(uriDescription, null, mContext.getPackageName());
            viewHolderCity.imageViewDescription1.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), imageResourceDescription1, null));


//            viewHolderCity.imageViewAvatar.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//
//                    City city = mCityList.get(position);
//                    city.setExpanded(!city.isExpanded());
//                    notifyItemChanged(position);
//
//                    recyclerView.smoothScrollToPosition(position);
//                }
//            });
//
//
//            viewHolderCity.imageViewDescription1.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    viewHolderCity.motionLayoutImage.setTransition(R.id.changeImage2_start, R.id.changeImage2_end);
//                    viewHolderCity.motionLayoutImage.transitionToEnd();
//                }
//            });
//            viewHolderCity.imageViewDescription2.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    viewHolderCity.motionLayoutImage.setTransition(R.id.changeImage3_start, R.id.changeImage3_end);
//                    viewHolderCity.motionLayoutImage.transitionToEnd();
//                }
//            });
//            viewHolderCity.imageViewDescription3.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    viewHolderCity.motionLayoutImage.setTransition(R.id.changeImage1_start, R.id.changeImage1_end);
//                    viewHolderCity.motionLayoutImage.transitionToEnd();
//                }
//            });
//            viewHolderCity.textViewDescriptionMenu.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    viewHolderCity.motionLayoutText.setTransition(R.id.changeDescription_start, R.id.changeDescription_end);
//                    viewHolderCity.motionLayoutText.transitionToEnd();
//                }
//            });
//            viewHolderCity.textViewPopulationMenu.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    viewHolderCity.motionLayoutText.setTransition(R.id.changePopulation_start, R.id.changePopulation_end);
//                    viewHolderCity.motionLayoutText.transitionToEnd();
//                }
//            });

            boolean isExpanded = mCityList.get(position).isExpanded();
            viewHolderCity.motionLayoutImage.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            viewHolderCity.motionLayoutText.setVisibility(isExpanded ? View.VISIBLE : View.GONE);


        } else if (holder instanceof ViewHolderLoading)
        {
            ViewHolderLoading viewHolderLoading = (ViewHolderLoading) holder;
            viewHolderLoading.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount()
    {
        return mCityList.size();
    }


    /**
     * View Holder Class
     */

    public class ViewHolderCity extends RecyclerView.ViewHolder
    {
        ConstraintLayout constraintLayoutParent;
        MotionLayout motionLayoutImage, motionLayoutText;
        ImageView imageViewAvatar, imageViewDescription1, imageViewDescription2, imageViewDescription3;
        TextView textViewTitle, textViewDescription, textViewPopulation, textViewWebAddress, textViewDescriptionMenu, textViewPopulationMenu;

        public ViewHolderCity(View itemView)
        {
            super(itemView);
            constraintLayoutParent = itemView.findViewById(R.id.constraintLayoutParent);
            motionLayoutImage = itemView.findViewById(R.id.motionLayoutImage);
            motionLayoutText = itemView.findViewById(R.id.motionLayoutText);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewDescription1 = itemView.findViewById(R.id.imageViewDescription1);
            imageViewDescription2 = itemView.findViewById(R.id.imageViewDescription2);
            imageViewDescription3 = itemView.findViewById(R.id.imageViewDescription3);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPopulation = itemView.findViewById(R.id.textViewPopulation);
            textViewDescriptionMenu = itemView.findViewById(R.id.textViewDescriptionMenu);
            textViewPopulationMenu = itemView.findViewById(R.id.textViewPopulationMenu);
            textViewWebAddress = itemView.findViewById(R.id.textViewWebAddress);

            imageViewAvatar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    City city = mCityList.get(getAdapterPosition());
                    city.setExpanded(!city.isExpanded());
                    notifyItemChanged(getAdapterPosition());

                    recyclerView.smoothScrollToPosition(getAdapterPosition());
                }
            });


            imageViewDescription1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    motionLayoutImage.setTransition(R.id.changeImage2_start, R.id.changeImage2_end);
                    motionLayoutImage.transitionToEnd();
                }
            });
            imageViewDescription2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    motionLayoutImage.setTransition(R.id.changeImage3_start, R.id.changeImage3_end);
                    motionLayoutImage.transitionToEnd();
                }
            });
            imageViewDescription3.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    motionLayoutImage.setTransition(R.id.changeImage1_start, R.id.changeImage1_end);
                    motionLayoutImage.transitionToEnd();
                }
            });
            textViewDescriptionMenu.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    motionLayoutText.setTransition(R.id.changeDescription_start, R.id.changeDescription_end);
                    motionLayoutText.transitionToEnd();
                }
            });
            textViewPopulationMenu.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    motionLayoutText.setTransition(R.id.changePopulation_start, R.id.changePopulation_end);
                    motionLayoutText.transitionToEnd();
                }
            });
        }
    }

    public class ViewHolderLoading extends RecyclerView.ViewHolder
    {

        MaterialProgressBar progressBar;

        public ViewHolderLoading(View itemView)
        {
            super(itemView);
            progressBar = itemView.findViewById(R.id.meZhanghai);
        }
    }
}
