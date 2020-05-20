package com.medulance.driver.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.medulance.driver.Interfaces.BookingHistoryInterface;
import com.medulance.driver.R;
import com.medulance.driver.models.BookingModel;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    Context context;
    List<BookingModel.Data> list;
    BookingHistoryInterface bookingHistoryInterface;

    public BookingHistoryAdapter(Context context, List<BookingModel.Data> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item_booking_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingModel.Data data = list.get(position);
        String dateTime = data.getPickup_date() + ", " + data.getPickup_time();
        holder.tv_dateTime.setText(dateTime);
        String ambulanceType = data.getAmbulance_type() + " Ambulance";
        holder.tv_type.setText(ambulanceType);
        String from = "FROM : " + data.getPickup_area();
        holder.tv_from.setText(from);
        String to = "TO : " + data.getDrop_area();
        holder.tv_to.setText(to);
        if (data.getStatus().equalsIgnoreCase("-1")) {
            holder.iv_status.setImageResource(R.drawable.cancelledride);
            holder.rb_rating.setVisibility(View.GONE);
        } else if ((data.getStatus().equalsIgnoreCase("3")) || (data.getStatus().equalsIgnoreCase("4"))) {
            holder.iv_status.setImageResource(R.drawable.paidwithcash);
            holder.rb_rating.setVisibility(View.VISIBLE);
        } else {
            holder.iv_status.setImageResource(R.drawable.pendingstatus);
            holder.rb_rating.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_dateTime;
        private TextView tv_type;
        private TextView tv_from;
        private TextView tv_to;
        private ImageView iv_status;
        private RatingBar rb_rating;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_dateTime = (TextView) itemView.findViewById(R.id.tv_dateTime);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_from = (TextView) itemView.findViewById(R.id.tv_from);
            tv_to = (TextView) itemView.findViewById(R.id.tv_to);
            iv_status = (ImageView) itemView.findViewById(R.id.iv_status);
            rb_rating = (RatingBar) itemView.findViewById(R.id.rb_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookingHistoryInterface.onClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnClickItem(BookingHistoryInterface bookingHistoryInterface) {
        this.bookingHistoryInterface = bookingHistoryInterface;
    }
}
