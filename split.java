import java.util.Scanner;

public class split {
    static int n;
    static int[] a=new int[10];

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        n=sc.nextInt();
        dfs(0,0,0);
    }
    //ansΪ��ǰ����֮��ĺͣ�kΪ��������ӵĸ�����tΪ��С������ֵ��Ϊ�˱����ظ���֮���������С����
    public static void dfs(int ans,int k,int t){
        if (ans==n){
            for (int i = 1; i < k; i++) {
                System.out.print(a[i]+"+");
            }
            System.out.println(a[k]);
        }
        for (int i = 1; i < n; i++) {
            if ((ans+i)<=n&&i>=t){
                a[k+1]=i;
                k++;
                dfs(ans+i,k,i);
                k--;
            }
        }
    }
}


