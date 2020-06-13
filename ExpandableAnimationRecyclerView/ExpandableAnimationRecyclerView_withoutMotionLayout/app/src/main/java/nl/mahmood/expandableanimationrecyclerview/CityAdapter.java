package nl.mahmood.expandableanimationrecyclerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
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

    public CityAdapter(Context context, List<City> cityList, RecyclerView recyclerView)
    {
        this.mContext = context;
        this.mCityList = cityList;



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
                }
            }
        });
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
        View view = null;
        if (viewType == 1)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            return new ViewHolderCity(view);
        } else if (viewType == 2)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_layout, parent, false);
            return new ViewHolderLoading(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof ViewHolderCity)
        {
            final ViewHolderCity viewHolderCity = (ViewHolderCity) holder;
            City city = mCityList.get(position);
            viewHolderCity.textViewTitle.setText(city.getTitle());
            viewHolderCity.textViewDescription.setText(city.getDescription());
            viewHolderCity.textViewWebAddress.setText(city.getWebsite());

            String uriAvatar = "@drawable/" + mCityList.get(position).getAvatar();
            int imageResourceAvatar = mContext.getResources().getIdentifier(uriAvatar, null, mContext.getPackageName());
            viewHolderCity.imageViewAvatar.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), imageResourceAvatar, null));

            String uriDescription = "@drawable/" + mCityList.get(position).getImage();
            int imageResourceDescription = mContext.getResources().getIdentifier(uriDescription, null, mContext.getPackageName());
            viewHolderCity.imageViewDescription.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), imageResourceDescription, null));

            boolean isExpanded = mCityList.get(position).isExpanded();
            viewHolderCity.constraintLayoutChild.setVisibility(isExpanded ? View.VISIBLE : View.GONE);



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
        ConstraintLayout constraintLayoutChild;
        ConstraintLayout constraintLayoutParent;
        ImageView imageViewAvatar, imageViewDescription;
        TextView textViewTitle, textViewDescription, textViewWebAddress;

        public ViewHolderCity(View itemView)
        {
            super(itemView);
            constraintLayoutParent = itemView.findViewById(R.id.constraintLayoutParent);
            constraintLayoutChild = itemView.findViewById(R.id.constraintLayoutChild);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            imageViewDescription = itemView.findViewById(R.id.imageViewDescription);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewWebAddress = itemView.findViewById(R.id.textViewWebAddress);

            constraintLayoutParent.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    City city = mCityList.get(getAdapterPosition());
                    city.setExpanded(!city.isExpanded());
                    notifyItemChanged(getAdapterPosition());
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
