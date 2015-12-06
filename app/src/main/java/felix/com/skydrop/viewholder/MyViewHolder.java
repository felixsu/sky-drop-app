package felix.com.skydrop.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fsoewito on 12/6/2015.
 * view holder abstract class
 * introduce bindViewHolder to bind complex view easier
 */
public abstract class MyViewHolder<T> extends RecyclerView.ViewHolder {
    public MyViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindViewHolder(T data, int position);
}
