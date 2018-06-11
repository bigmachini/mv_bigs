package com.copia.copiaandroid.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.copia.copiaandroid.R;
import com.copia.copiaandroid.activities.BaseActivity;
import com.copia.copiaandroid.activities.VideoActivity;
import com.copia.copiaandroid.db.controllers.ProductController;
import com.copia.copiaandroid.db.entities.ProductEntity;
import com.copia.copiaandroid.dialogs.ProductDetailDialog;
import com.copia.copiaandroid.structures.LoginStructure;
import com.copia.copiaandroid.utils.Constants;
import com.copia.copiaandroid.utils.ShoppingCart;
import com.copia.copiaandroid.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final Context mContext;
    private List<ProductEntity> mProducts;
    private String fileDir;
    ProductController mProductController;
    private File videoRoot;
    ShoppingCart mShoppingCart;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0");
    ProductDetailDialog productDetailDialog;

    public ProductAdapter(Context context, int categoryId) {
        this.mContext = context;
        mProductController = new ProductController(mContext);
        this.mProducts = mProductController.getProductByCategory(categoryId);
        mShoppingCart = ShoppingCart.getInstance();
        ContextWrapper cw = new ContextWrapper(context);
        fileDir = cw.getDir("imageDir", Context.MODE_PRIVATE).getAbsolutePath();
        videoRoot = android.os.Environment.getExternalStorageDirectory();

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        RelativeLayout relativeLayout;
        LinearLayout llOriginalPrice;
        LinearLayout linearLayout;
        ImageView productImageView;
        ImageView productfeaturedImageView;
        ImageView videoImageView;
        TextView productNameTextView;
        TextView productCodeTextView;
        TextView tvOriginalPrice;
        TextView tvNewPrice;
        TextView savingsTextView;
        Button addToCartButton;
        Button btnPricelist;


        public ViewHolder(View view) {
            super(view);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_cell_product);
            llOriginalPrice = (LinearLayout) view.findViewById(R.id.ll_original);
            linearLayout = (LinearLayout) view.findViewById(R.id.ll_okoa);
            productImageView = (ImageView) view.findViewById(R.id.iv_product);
            productfeaturedImageView = (ImageView) view.findViewById(R.id.iv_featured_icon);
            videoImageView = (ImageView) view.findViewById(R.id.iv_video_icon);
            productNameTextView = (TextView) view.findViewById(R.id.tv_product_name);
            productCodeTextView = (TextView) view.findViewById(R.id.tv_code);
            tvOriginalPrice = (TextView) view.findViewById(R.id.tv_original_price);
            tvNewPrice = (TextView) view.findViewById(R.id.tv_new_price);
            savingsTextView = (TextView) view.findViewById(R.id.tv_savings);
            addToCartButton = (Button) view.findViewById(R.id.btn_add_to_cart);
            btnPricelist = (Button) view.findViewById(R.id.btn_pricelist);

        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ProductEntity product = mProducts.get(position);
        //images
        String filePath = this.fileDir + File.separator + Constants.PROD_PREFIX + product.getRemoteId() + ".jpg";
        File imageFile = new File(filePath);

        if (!imageFile.exists()) {
            filePath = this.fileDir + File.separator + Constants.PROD_PREFIX + product.getRemoteId() + ".png";
        }
        imageFile = new File(filePath);
        Uri imageUri = Uri.fromFile(imageFile);
        holder.productImageView.setImageURI(imageUri);

        final File videoFile = new File(videoRoot.getAbsolutePath() + "/COPIA/" + Constants.PRODUCT_VIDEO_PREFIX + "-" + product.getRemoteId() + ".mp4");

        if (videoFile.exists()) {
            Log.e("product_video", product.getName() + " EXISTS");
            holder.videoImageView.setVisibility(View.VISIBLE);
        } else {
            Log.e("product_video", product.getRemoteId() + "DOESNT EXISTS");
            holder.videoImageView.setVisibility(View.INVISIBLE);
        }
        holder.videoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, VideoActivity.class);
                i.putExtra("video_path", videoFile.toString());
                mContext.startActivity(i);
            }
        });

        holder.productNameTextView.setText(product.getName());
        holder.tvNewPrice.setText("KSH. " + mDecimalFormat.format(product.getPrice()));
        holder.productCodeTextView.setText("Code: " + product.getCode());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productDetailDialog == null) {
                    productDetailDialog = new ProductDetailDialog(mContext, product);
                    productDetailDialog.show();
                } else {
                    if (!productDetailDialog.isShowing()) {
                        productDetailDialog = new ProductDetailDialog(mContext, product);
                        productDetailDialog.show();
                    }
                }
            }
        });

        if (LoginStructure.getLoginResponse(mContext).agentData.isSalesRep) {
//            holder.btnPricelist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Utils.setStringSetting(mContext, Constants._PRODUCT_SELECTED, String.valueOf(product.getRemoteId()));
//                    mContext.startActivity(new Intent(mContext, PriceListActivity.class));
//                }
//            });
        } else {
            holder.btnPricelist.setVisibility(View.GONE);
        }

        holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShoppingCart.add(product, 1);
                updateCartCount();
                Utils.toastText(mContext, "Product Added To Cart");
            }
        });
        //show featured product
        if (product.isFeatured()) {
            holder.relativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.featured_grid_background));
            holder.productfeaturedImageView.setVisibility(View.VISIBLE);
        } else {
            holder.relativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.et_shape_product_bg));
            holder.productfeaturedImageView.setVisibility(View.INVISIBLE);
        }

        //promotion
        if (product.isOnPromotion()) {
            holder.relativeLayout.setVisibility(View.VISIBLE);
            try {
                holder.savingsTextView.setText(String.valueOf(product.getSavings())
                        .replace(".0", "") + "/-");
            } catch (Exception e) {
                holder.savingsTextView.setText(String.valueOf(product.getSavings()));
            }
        }
        if (product.getSavings() > 0.0) {
            holder.productNameTextView.setText(product.getName());
            Double newPrice = Double.parseDouble(String.valueOf(product.getPrice()));
            Double savings = Double.parseDouble(String.valueOf(product.getSavings()));
            int originalPrice = (int) (newPrice + savings);
            holder.tvOriginalPrice.setText("KSH. " + originalPrice);
            holder.tvNewPrice.setText("KSH. " + mDecimalFormat.format(product.getPrice()));
            holder.productCodeTextView.setText("Code: " + product.getCode());
        } else {
            holder.llOriginalPrice.setVisibility(View.INVISIBLE);
            holder.tvNewPrice.setText("KSH. " + mDecimalFormat.format(product.getPrice()));
        }

    }

    private void updateCartCount() {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).updateCartBadge();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mProducts.size();
    }
}
