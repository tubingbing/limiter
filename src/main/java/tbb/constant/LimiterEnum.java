package tbb.constant;

/**
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 13:06
 */
public enum LimiterEnum {

    SIMPLE_COUNT(1), //简单计数
    SMOOTH_COUNT(2), //平滑窗口计数
    LEAKY_BUCKET(3), //漏桶
    TOKEN_BUCKET(4); //令牌桶

    public int value;
    LimiterEnum (int value){
        this.value = value;
    }

}
