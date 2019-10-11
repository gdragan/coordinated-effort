package com.example.android.coordinatedeffort;

import android.content.Context;
import android.graphics.Rect;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleHolder> {

    public static class SimpleHolder extends RecyclerView.ViewHolder {
        public final TextView text;

        public SimpleHolder(View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.text);
            itemView.setClickable(true);
        }
    }

    private static final String[] ITEMS = {
            "Alpha", "Beta", "Cupcake", "Donut",
            "Eclair", "FroYo", "Gingerbread", "Honeycomb",
            "Ice Cream Sandwich", "Jelly Bean", "KitKat",
            "Lollipop", "Marshmallow", "Nougat", "Oreo", "Pie", "Q", "Nobody Knows"
    };

    private LayoutInflater mInflater;

    public SimpleAdapter(Context context) {
        mInflater = LayoutInflater.from(context);

    }

    public SimpleAdapter(RecyclerView recyclerView) {
        this(recyclerView.getContext());
        //Apply card margins with a decoration
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                final int position = parent.getChildViewHolder(view).getAdapterPosition();
                final int offset = parent.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);
                outRect.set(offset, position == 0 ? offset : 0, offset, offset);
            }
        });
    }

    public static void setAdapter(NestedScrollView scrollView) {
        SimpleAdapter adapter = new SimpleAdapter(scrollView.getContext());
        ViewGroup container = (LinearLayout) scrollView.getChildAt(0);
        final int offset = container.getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin);

        for (int position = 0; position < ITEMS.length; position++) {
            SimpleHolder viewHolder = adapter.onCreateViewHolder(container, adapter.getItemViewType(position));
            adapter.onBindViewHolder(viewHolder, position);
            container.addView(viewHolder.itemView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.itemView.getLayoutParams();
            layoutParams.setMargins(offset, position == 0 ? offset : 0, offset, offset);
            viewHolder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public SimpleAdapter.SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleHolder(mInflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SimpleHolder viewHolder, int position) {
        viewHolder.text.setText(ITEMS[position]);
    }

    @Override
    public int getItemCount() {
        return ITEMS.length;
    }
}
