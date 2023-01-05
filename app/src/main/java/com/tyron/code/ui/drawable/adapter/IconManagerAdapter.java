package com.tyron.code.ui.drawable.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.tyron.code.R;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import java.io.FileInputStream;
import android.graphics.drawable.PictureDrawable;
import java.io.FileNotFoundException;
import java.io.File;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import android.widget.Toast;
import com.tyron.code.ui.drawable.Icons;

public class IconManagerAdapter extends RecyclerView.Adapter<IconManagerAdapter.ViewHolder> {

    private static final int TYPE_EMPTY = -1;
    private static final int TYPE_ITEM = 1;

    public interface OnIconSelectedListener {
        void onIconSelect(Icons icons);
    }
    
    private final List<Icons> mIcons = new ArrayList<>();
    private OnIconSelectedListener mListener;

    public IconManagerAdapter() {

    }

    public void setOnIconSelectedListener(OnIconSelectedListener listener) {
        mListener = listener;
    }

    public void submitList(@NonNull List<Icons> icons) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mIcons.size();
                }

                @Override
                public int getNewListSize() {
                    return icons.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mIcons.get(oldItemPosition).equals(icons.get(newItemPosition));
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mIcons.get(oldItemPosition).equals(icons.get(newItemPosition));
                }
            });
        mIcons.clear();
        mIcons.addAll(icons);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout root = new FrameLayout(parent.getContext());
        root.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                           ViewGroup.LayoutParams.WRAP_CONTENT));
        final ViewHolder holder;
        if (viewType == TYPE_EMPTY) {
            holder = new EmptyViewHolder(root);
        } else {
            holder = new ItemViewHolder(root);
        }

        root.setOnClickListener(v -> {
            if (mListener != null) {
                int position = holder.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onIconSelect(mIcons.get(position));                 
                }
            }
        });
        
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            ((ItemViewHolder) holder).bind(mIcons.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mIcons.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mIcons.isEmpty()) {
            return TYPE_EMPTY;
        }

        return TYPE_ITEM;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
        }
    }

    private static class ItemViewHolder extends ViewHolder {

        public ImageView icon;
        public TextView title;
        private Context context;
        public ItemViewHolder(FrameLayout view) {
            super(view);
            LayoutInflater.from(view.getContext())
                .inflate(R.layout.icon_manager_item, view);
            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
        }

        public void bind(Icons icons) {
            String name = icons.getRootFile().getName();
            Drawable drawable = null;
            if (name.endsWith(".svg")){
            title.setText(name.replace(".svg", ""));
            } else if (name.endsWith(".xml"))    {
            title.setText(name.replace(".xml", ""));   
            }
            try {
            FileInputStream fileInputStream = new FileInputStream(new File(icons.getRootFile().getAbsolutePath()));

            try {

                SVG svg = SVG.getFromInputStream(fileInputStream);

                drawable = new PictureDrawable(svg.renderToPicture());
                icon.setImageDrawable(drawable);
                
            } catch (SVGParseException e) {

            }

        } catch (FileNotFoundException e) {

        }
        
    }        
          
    }
    private static class EmptyViewHolder extends ViewHolder {

        public final TextView text;

        public EmptyViewHolder(FrameLayout view) {
            super(view);

            text = new TextView(view.getContext());
            text.setTextSize(18);
            text.setText(R.string.icon_manager_empty);
            view.addView(text, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                            FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        }
    }
  

    
}
