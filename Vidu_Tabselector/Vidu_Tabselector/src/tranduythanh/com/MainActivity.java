package tranduythanh.com;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//Enum để thực hiện phép toán
	enum Operator
	{
		Cong,//phép cộng
		Tru,//phép trừ
		Nhan,//phép nhân
		Chia//phép chia
	}
	Button btncong,btntru,btnnhan,btnchia;
	EditText editsoa,editsob;
	TextView txtkq;
	ListView lvHistory;
	ArrayList<String>array_operator=new ArrayList<String>();
	ArrayAdapter<String>adapter=null;
	//Variable in listener
	OnClickListener myclick=new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			switch(arg0.getId())
			{
			case R.id.btncong:
			{
				processOperator(Operator.Cong);
			}
			break;
			case R.id.btntru:
			{
				processOperator(Operator.Tru);
			}
			break;
			case R.id.btnnhan:
			{
				processOperator(Operator.Nhan);
			}
			break;
			case R.id.btnchia:
			{
				processOperator(Operator.Chia);
			}
		 }
		}
	};
	/**
	 * Hàm xử lý phép toán theo operator
	 * @param op
	 */
	public void processOperator(Operator op)
	{
		String sa=editsoa.getText()+"";
		String sb=editsob.getText().toString();
		int a=Integer.parseInt(sa);
		int b=Integer.parseInt(sb);
		String kq="";
		switch(op)
		{
		case Cong:
			kq=a+" + "+b +" = "+(a+b);
			break;
		case Tru:
			kq=a+" - "+b +" = "+(a-b);
			break;
		case Nhan:
			kq=a+" * "+b +" = "+(a*b);
			break;
		case Chia:
			if(b!=0)
				kq=a+" / "+b +" = "+(a*1.0/b);
			else
				kq="b phai khac 0";
			break;
		default:
			kq="Invalid operator!";
		}
		txtkq.setText(kq);
		array_operator.add(kq);
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadTabs();
		doFormWidgets();
	}
	//Cấu hình tab
	public void loadTabs()
	{
		//Lấy Tabhost id ra trước (cái này của built - in android
		final TabHost tab=(TabHost) findViewById
				(android.R.id.tabhost);
		//gọi lệnh setup
		tab.setup();
		TabHost.TabSpec spec;
		//Tạo tab1
		spec=tab.newTabSpec("t1");
		spec.setContent(R.id.tab1);
		spec.setIndicator("1-Calculator");
		tab.addTab(spec);
		//Tạo tab2
		spec=tab.newTabSpec("t2");
		spec.setContent(R.id.tab2);
		spec.setIndicator("2-History");
		tab.addTab(spec);
		//Thiết lập tab mặc định được chọn ban đầu là tab 0
		tab.setCurrentTab(0);
		//Ở đây Tôi để sự kiện này để các bạn tùy xử lý
		//Ví dụ tab1 chưa nhập thông tin xong mà lại qua tab 2 thì báo...
		tab.setOnTabChangedListener(new 
				TabHost.OnTabChangeListener() {
			public void onTabChanged(String arg0) {
				String s="Tab tag ="+arg0 +"; index ="+
						tab.getCurrentTab();
				Toast.makeText(getApplicationContext(), 
						s, Toast.LENGTH_LONG).show();}
		});
	}
	//Khởi tạo các đối tượng và gán ADapter cho ListView
	public void doFormWidgets()
	{
		btncong=(Button) findViewById(R.id.btncong);
		btntru=(Button) findViewById(R.id.btntru);
		btnnhan=(Button) findViewById(R.id.btnnhan);
		btnchia=(Button) findViewById(R.id.btnchia);
		editsoa=(EditText) findViewById(R.id.editsoa);
		editsob=(EditText) findViewById(R.id.editsob);
		txtkq=(TextView) findViewById(R.id.txtketqua);
		lvHistory=(ListView) findViewById(R.id.lvhistory);
		btncong.setOnClickListener(myclick);
		btntru.setOnClickListener(myclick);
		btnnhan.setOnClickListener(myclick);
		btnchia.setOnClickListener(myclick);
		adapter=new ArrayAdapter<String>
		(this, 
				android.R.layout.simple_list_item_1, 
				array_operator);
		lvHistory.setAdapter(adapter);
	}
}
