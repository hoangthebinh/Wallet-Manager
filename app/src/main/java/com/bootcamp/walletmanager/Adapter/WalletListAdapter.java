package com.bootcamp.walletmanager.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bootcamp.walletmanager.Datamodel.Wallets;
import com.bootcamp.walletmanager.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletListViewHolder> {
    List<Wallets> mWallets;
    Context mContext;
    WalletSelected mWalletSelected;


    public WalletListAdapter(Context context, List<Wallets> wallets, WalletSelected walletSelected) {
        mWallets = wallets;
        mContext = context;
        mWalletSelected = walletSelected;
    }

    @NonNull
    @Override
    public WalletListAdapter.WalletListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardView itemView = (CardView) layoutInflater.inflate(R.layout.wallet_list_item, parent, false);
        return new WalletListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletListAdapter.WalletListViewHolder holder, final int position) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
        holder.background.setBackgroundColor(color);

        holder.walletName.setText(mWallets.get(position).getName());
        holder.money.setText(mWallets.get(position).getAmount() + ".00 đ");

        if (mWallets.get(position).getDayCreated() != null) {
            String currentTime = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(mWallets.get(position).getDayCreated());
            holder.date.setText(currentTime);
        }

        int walletType = mWallets.get(position).getWalletType();
        if (walletType == Wallets.WalletType.NORMAL.getValue()) {
            holder.type.setText("Ví thường");
            holder.typeImg.setImageResource(R.drawable.wallet_wallet_icon);
        }
        else if (walletType == Wallets.WalletType.BANK_ACCOUNT.getValue()) {
            holder.type.setText("Tài khoản ngân hàng");
            holder.typeImg.setImageResource(R.drawable.wallet_bank);
        }
        else if (walletType == Wallets.WalletType.CREDIT_CARD.getValue()) {
            holder.type.setText("Thẻ tín dụng");
            holder.typeImg.setImageResource(R.drawable.wallet_credit_card);
        }
        else if (walletType == Wallets.WalletType.SAVINGS.getValue()) {
            holder.type.setText("Ví tiết kiệm");
            holder.typeImg.setImageResource(R.drawable.wallet_saving);
        }
        else if (walletType == Wallets.WalletType.ALL.getValue()) {
            holder.type.setText("");
            holder.typeImg.setImageResource(R.drawable.global_icon);
        }

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWalletSelected.onWalletSelected(mWallets.get(position));
            }
        });
    }

    public interface WalletSelected {
        void onWalletSelected(Wallets wallet);
    }

    @Override
    public int getItemCount() {
        return mWallets.size();
    }

    public class WalletListViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView walletName;
        TextView money;
        TextView date;
        TextView type;
        ImageView typeImg;
        LinearLayout background;

        WalletListViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.wallet_list_cardview);
            walletName = (TextView) itemView.findViewById(R.id.wallet_name);
            money = (TextView) itemView.findViewById(R.id.wallet_amount);
            date = (TextView) itemView.findViewById(R.id.wallet_date);
            type = (TextView) itemView.findViewById(R.id.wallet_type);
            typeImg = (ImageView) itemView.findViewById(R.id.wallet_list_img);
            background = (LinearLayout) itemView.findViewById(R.id.wallet_list_background);

        }
    }

}
