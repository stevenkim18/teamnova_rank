package com.teamnova.teamnova_rank;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class RankRecyclerviewAdapter extends RecyclerView.Adapter<RankRecyclerviewAdapter.MyViewholder>{


    private List<RankData> rankDataList; //랭크데이터 리스트 변수
    private OnclickItemListener onClickItemListener;

    private Context context;
    private Boolean isToolTipshowed;


    //아이템 을 클릭했을때 onClickItemListener메소드 실행
    public void setOnClickItemListener(
            OnclickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public RankRecyclerviewAdapter(Context context, List<RankData> rank_data_list) {
        this.rankDataList = rank_data_list;
        this.context = context;
        isToolTipshowed = true;
    }

    public interface OnclickItemListener{
        void clickDetaiInfo(RankData rankData);
    }

    //배열 사이즈 정해주기
    @Override
    public int getItemCount() {
        return rankDataList.size();
    }


    //MyViewholder 클래스정의
    public class MyViewholder extends RecyclerView.ViewHolder{

        TextView rankTitle,rankPoint;  //rankTitle:랭크 목록에 아이템에 있는 제목  ,rankPoint :랭크목록에 아이템에 있는 점수
        ImageView rankImage;//rankImage:랭크 목록 아이템에 있는 썸네일 이미지
        LottieAnimationView goldLottieAnimation,silverLottieAnimation,brownLottieAnimation;//goldLottieAnimation:금색트로피 모양의 로띠
        ConstraintLayout rankNormalCl;//리사이클러뷰에 들어가는 아이템 컨스트레인트레이아웃

        public MyViewholder(View itemview){

            super(itemview);
            rankNormalCl = itemview.findViewById(R.id.rank_item_cl);
            goldLottieAnimation = itemview.findViewById(R.id.gold_lottie_animation);
            silverLottieAnimation = itemview.findViewById(R.id.silver_lottie_animation);
            brownLottieAnimation = itemview.findViewById(R.id.brown_lottie_animation);

            rankTitle = itemview.findViewById(R.id.title_textView);
            rankPoint = itemview.findViewById(R.id.ranking_point_textView);
            rankImage = itemview.findViewById(R.id.imageView);

        }

    }



    @Override
    public int getItemViewType(int position) {
        RankData item = rankDataList.get(position);

        //랭크 목록에 아이템이 랭킹3위 안에 드는 경우
        if( item.getRanking() <= 3){

            return 0;

        }
        //랭크 목록에 아이템이 랭킹 3위 미만인 경우
        else{
            return 1;
        }
    }

    @Override
    public RankRecyclerviewAdapter.MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        //랭크 목록에 아이템이 랭킹3위 안에 드는 경우  받아온 return 값이 0인경우
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_top, parent, false);

            //item_rank_top 레이아웃 을보여준다
            return new MyViewholder(view);
        }
        //랭크 목록에 아이템이 랭킹3위 밖인 경우  받아온 return 값이 1인경우
        else if (viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank_normal, parent, false);

            //item_rank_normal 레이아웃 을보여준다
            return new MyViewholder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RankRecyclerviewAdapter.MyViewholder holder, int position) {

        final RankData data = rankDataList.get(position);//위치에 따라서 그에 맞는 데이터를 얻어오게 한다.

        //랭킹이 1위인 작품을 보여줄 때 로띠중에서 금색트로피 로띠만 보이도록 합니다
        if(data.getRanking()<=1){
            holder.goldLottieAnimation.setVisibility(View.VISIBLE);
            holder.silverLottieAnimation.setVisibility(View.INVISIBLE);
            holder.brownLottieAnimation.setVisibility(View.INVISIBLE);

        }
        //랭킹이 2위인 작품을 보여줄 때 로띠중에서 은색트로피 로띠만 보이도록 합니다
        else if(data.getRanking()==2){
            holder.goldLottieAnimation.setVisibility(View.INVISIBLE);
            holder.silverLottieAnimation.setVisibility(View.VISIBLE);
            holder.brownLottieAnimation.setVisibility(View.INVISIBLE);

        }
        //랭킹이 3위인 작품을 보여줄 때 로띠중에서 동색트로피 로띠만 보이도록 합니다
        else if(data.getRanking()==3){
            holder.goldLottieAnimation.setVisibility(View.INVISIBLE);
            holder.silverLottieAnimation.setVisibility(View.INVISIBLE);
            holder.brownLottieAnimation.setVisibility(View.VISIBLE);
        }

        //앞서 뷰홀더에 세팅해준 것을 각 위치에 맞는 것들로 보여주게 하기 위해서 세팅해준다.
        holder.rankTitle.setText(data.getRankTitle()); //제목 보여주기
        holder.rankPoint.setText(data.getRankingPoint()+"");//점수 보여주기
        if(position == 0){
            showTooltip(holder.rankPoint, isToolTipshowed);
        }


        //랭크목록에 있는 아이템을 선택했을때
        holder.rankNormalCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItemListener.clickDetaiInfo(data);
            }
        });

//      holder.rank_image.setImageURI(data.get());

    }

    // toolTip을 보여줌.
    private void showTooltip(View view, Boolean isToolTipshowed){

        // 처음에 툴팁을 한번만 보여주기 위해서 사용.
        this.isToolTipshowed = false;

        if(isToolTipshowed){
            new SimpleTooltip.Builder(context)
                    .anchorView(view)
                    .text("Ranking Point")                 // 툴팁 메시지
                    .gravity(Gravity.TOP)                  // 툴팁 방향
                    .animated(true)
                    .transparentOverlay(false)             // 배경을 회색으로 할껀지
                    .build()
                    .show();

        }

    }
}