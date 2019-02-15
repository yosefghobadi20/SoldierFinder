package com.nikosoft.soldierfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yosef on 18/03/2017.
 */
class Soldier_adapter extends RecyclerView.Adapter<Soldier_adapter.ViewHolder> {


    private Context _context;
    private List<Soldier_Info> soldier;
    private LayoutInflater layoutInflater;
    private Soldier_Info _soldier;
    private G g=new G();

    public Soldier_adapter(Context context, ArrayList<Soldier_Info> soldier)
    {
        this._context=context;
        this.soldier=soldier;
        this.layoutInflater=LayoutInflater.from(this._context);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=layoutInflater.inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        _soldier=soldier.get(position);

        holder.name.setText(_soldier.Name+" "+_soldier.Family);
        holder.cur.setText("از : "+_soldier.CurrentState+" , "+_soldier.CurrentCity);
        holder.req.setText("به : " + _soldier.RequestState + " , " + _soldier.RequestCity);

        if(_soldier.Organ==1)
            holder.img.setImageResource(R.drawable.artesh);
        else if(_soldier.Organ==2)
            holder.img.setImageResource(R.drawable.sepah);
        else
            holder.img.setImageResource(R.drawable.naja);

        holder.request_date.setText(Utility.farsinumber(_soldier.RequestDate));
        /*holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(soldier.get(position).Favorite)
                {
                    holder.img_fav.setImageResource(R.drawable.fav_off);

                }
                else
                {
                    holder.img_fav.setImageResource(R.drawable.fav_on);

                }
            }
        });*/
        /*if(_soldier.Favorite)
            holder.img_fav.setImageResource(R.drawable.fav_on);
        else
            holder.img_fav.setImageResource(R.drawable.fav_off);*/


        holder.materialRippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(G.CurrentActivity,Details.class);
                intent.putExtra("ID",soldier.get(position).ID);
                intent.putExtra("Name",soldier.get(position).Name);
                intent.putExtra("Family",soldier.get(position).Family);
                intent.putExtra("RequestState",soldier.get(position).RequestState);
                intent.putExtra("RequestCity",soldier.get(position).RequestCity);
                intent.putExtra("RequestPadegan",soldier.get(position).RequestPadegan);
                intent.putExtra("CurrentState",soldier.get(position).CurrentState);
                intent.putExtra("CurrentCity",soldier.get(position).CurrentCity);
                intent.putExtra("CurrentPadegan",soldier.get(position).CurrentPadegan);
                intent.putExtra("Send_OutDate",Utility.farsinumber(soldier.get(position).Send_OutDate));
                intent.putExtra("RequestDate",Utility.farsinumber(soldier.get(position).RequestDate));
                intent.putExtra("Phone",Utility.farsinumber(soldier.get(position).Phone));
                intent.putExtra("CurrentStatus",soldier.get(position).CurrentStatus);
                intent.putExtra("MovmentStatus",soldier.get(position).MovmentStatus);
                intent.putExtra("Rate",soldier.get(position).Rate);
                intent.putExtra("Organ",soldier.get(position).Organ);
                intent.putExtra("Sub_Organ",soldier.get(position).Sub_Organ);
                //intent.putExtra("Description",soldier.get(position).Description);
                G.CurrentActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return soldier.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name,req,request_date;
        private TextView cur;
        private ImageView img,img_fav;
        private MaterialRippleLayout materialRippleLayout;

        public ViewHolder(final View itemView) {
            super(itemView);
            name= (TextView) itemView.findViewById(R.id.txt_name);
            //Typeface typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/iransans.ttf");
            cur= (TextView) itemView.findViewById(R.id.txt_cur_padegan);
            req= (TextView) itemView.findViewById(R.id.txt_req_padegan);
            request_date= (TextView) itemView.findViewById(R.id.txt_request_date);
            img= (ImageView) itemView.findViewById(R.id.img);
            //img_fav= (ImageView) itemView.findViewById(R.id.img_fav);
            materialRippleLayout= (MaterialRippleLayout) itemView.findViewById(R.id.lay_ripple);
        }
    }



}
