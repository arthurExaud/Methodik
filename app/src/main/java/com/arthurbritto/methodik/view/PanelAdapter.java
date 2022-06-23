package com.arthurbritto.methodik.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurbritto.methodik.R;
import com.arthurbritto.methodik.model.Panel;

import java.util.List;

import static com.arthurbritto.methodik.model.Utils.colorSelector;

/**
 * Adapter for the RecyclerView that displays a Panel of lists.
 */
public class PanelAdapter extends RecyclerView.Adapter<PanelAdapter.PanelViewHolder> {

    public interface ClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }

    private final LayoutInflater inflater;
    private List<Panel> panels; // Cached copy of panels
    private ClickListener clickListener;

    PanelAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public PanelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PanelViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PanelViewHolder holder, int position) {
        if (panels != null) {
            Panel current = panels.get(position);
            holder.panelItemView.setText(current.getName());
            holder.panelViewColor.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorSelector(current.getColor())));
        }
    }

    /**
     * Associates a panel of lists with this adapter
     */
    void updatePanels(List<Panel> panels) {
        this.panels = panels;
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * panels has not been updated (means initially, it's null, and we can't return null).
     */
    @Override
    public int getItemCount() {
        return (panels != null) ? panels.size() : 0;
    }

    /**
     * Gets the panel at a given position.
     * This method is useful for identifying which panel
     * was clicked or swiped in methods that handle user events.
     *
     * @param position The position of the panel in the RecyclerView
     * @return The panel at the given position
     */
    public Panel getPanelAtPosition(int position) {
        return panels.get(position);
    }

    class PanelViewHolder extends RecyclerView.ViewHolder {
        private final TextView panelItemView;
        private final View panelViewColor;

        private PanelViewHolder(View itemView) {
            super(itemView);
            panelItemView = itemView.findViewById(R.id.textView);
            panelViewColor = itemView.findViewById(R.id.recycler_view_color);
            itemView.setOnClickListener(view -> clickListener.onItemClick(view, getAdapterPosition()));
            itemView.setOnLongClickListener(view -> {
                clickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
