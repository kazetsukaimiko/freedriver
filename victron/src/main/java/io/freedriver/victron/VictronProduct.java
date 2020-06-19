package io.freedriver.victron;


import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Enum containing all known Victron Energy Products by hex productId.
 */
public enum VictronProduct {
    BMV_600S(ProductType.BMV, 0x0200, "BMV-600S"),
    BMV_602S(ProductType.BMV, 0x0201, "BMV-602S"),
    BMV_600HS(ProductType.BMV, 0x0202, "BMV-600HS"),
    BMV_712(ProductType.BMV, 0xA381, "BMV-712"),
    BMV_700(ProductType.BMV, 0x203, "BMV-700"),
    BMV_702(ProductType.BMV, 0x204, "BMV-702"),
    BMV_700H(ProductType.BMV,0x205, "BMV-700H"),
    BLUESOLAR_MPPT_70_15_DISCONTINUED(ProductType.MPPT, 0x0300, "BlueSolar MPPT 70|15", true),
    BLUESOLAR_MPPT_75_50_DISCONTINUED(ProductType.MPPT, 0xA040, "BlueSolar MPPT 75|50", true),
    BLUESOLAR_MPPT_150_35_DISCONTINUED(ProductType.MPPT, 0xA041, "BlueSolar MPPT 150|35", true),
    BLUESOLAR_MPPT_75_15(ProductType.MPPT, 0xA042, "BlueSolar MPPT 75|15"),
    BLUESOLAR_MPPT_100_15(ProductType.MPPT, 0xA043, "BlueSolar MPPT 100|15"),
    BLUESOLAR_MPPT_100_30_DISCONTINUED(ProductType.MPPT, 0xA044, "BlueSolar MPPT 100|30", true),
    BLUESOLAR_MPPT_100_50_DISCONTINUED(ProductType.MPPT, 0xA045, "BlueSolar MPPT 100|50", true),
    BLUESOLAR_MPPT_150_70(ProductType.MPPT, 0xA046, "BlueSolar MPPT 150|70"),
    BLUESOLAR_MPPT_150_100(ProductType.MPPT, 0xA047, "BlueSolar MPPT 150|100"),
    BLUESOLAR_MPPT_100_50_REV2(ProductType.MPPT, 0xA049, "BlueSolar MPPT 100|50 rev2"),
    BLUESOLAR_MPPT_100_30_REV2(ProductType.MPPT, 0xA04A, "BlueSolar MPPT 100|30 rev2"),
    BLUESOLAR_MPPT_150_35_REV2(ProductType.MPPT, 0xA04B, "BlueSolar MPPT 150|35 rev2"),
    BLUESOLAR_MPPT_75_10(ProductType.MPPT, 0xA04C, "BlueSolar MPPT 75|10"),
    BLUESOLAR_MPPT_150_45(ProductType.MPPT, 0xA04D, "BlueSolar MPPT 150|45"),
    BLUESOLAR_MPPT_150_60(ProductType.MPPT, 0xA04E, "BlueSolar MPPT 150|60"),
    BLUESOLAR_MPPT_150_85(ProductType.MPPT, 0xA04F, "BlueSolar MPPT 150|85"),
    SMARTSOLAR_MPPT_250_100(ProductType.MPPT, 0xA050, "SmartSolar MPPT 250|100"),
    SMARTSOLAR_MPPT_150_100_DISCONTINUED(ProductType.MPPT, 0xA051, "SmartSolar MPPT 150|100", true),
    SMARTSOLAR_MPPT_150_85_DISCONTINUED(ProductType.MPPT, 0xA052, "SmartSolar MPPT 150|85", true),
    SMARTSOLAR_MPPT_75_15(ProductType.MPPT, 0xA053, "SmartSolar MPPT 75|15"),
    SMARTSOLAR_MPPT_75_10(ProductType.MPPT, 0xA054, "SmartSolar MPPT 75|10"),
    SMARTSOLAR_MPPT_100_15(ProductType.MPPT, 0xA055, "SmartSolar MPPT 100|15"),
    SMARTSOLAR_MPPT_100_30(ProductType.MPPT, 0xA056, "SmartSolar MPPT 100|30"),
    SMARTSOLAR_MPPT_100_50(ProductType.MPPT, 0xA057, "SmartSolar MPPT 100|50"),
    SMARTSOLAR_MPPT_150_35(ProductType.MPPT, 0xA058, "SmartSolar MPPT 150|35"),
    SMARTSOLAR_MPPT_150_100_REV2(ProductType.MPPT, 0xA059, "SmartSolar MPPT 150|100 rev2"),
    SMARTSOLAR_MPPT_150_85_REV2(ProductType.MPPT, 0xA05A, "SmartSolar MPPT 150|85 rev2"),
    SMARTSOLAR_MPPT_250_70(ProductType.MPPT, 0xA05B, "SmartSolar MPPT 250|70"),
    SMARTSOLAR_MPPT_250_85(ProductType.MPPT, 0xA05C, "SmartSolar MPPT 250|85"),
    SMARTSOLAR_MPPT_250_60(ProductType.MPPT, 0xA05D, "SmartSolar MPPT 250|60"),
    SMARTSOLAR_MPPT_250_45(ProductType.MPPT, 0xA05E, "SmartSolar MPPT 250|45"),
    SMARTSOLAR_MPPT_100_20(ProductType.MPPT, 0xA05F, "SmartSolar MPPT 100|20"),
    SMARTSOLAR_MPPT_100_20_48V(ProductType.MPPT, 0xA060, "SmartSolar MPPT 100|20 48V"),
    SMARTSOLAR_MPPT_150_45(ProductType.MPPT, 0xA061, "SmartSolar MPPT 150|45"),
    SMARTSOLAR_MPPT_150_60(ProductType.MPPT, 0xA062, "SmartSolar MPPT 150|60"),
    SMARTSOLAR_MPPT_150_70(ProductType.MPPT, 0xA063, "SmartSolar MPPT 150|70"),
    SMARTSOLAR_MPPT_250_85_REV2(ProductType.MPPT, 0xA064, "SmartSolar MPPT 250|85 rev2"),
    SMARTSOLAR_MPPT_250_100_REV2(ProductType.MPPT, 0xA065, "SmartSolar MPPT 250|100 rev2"),
    SMARTSOLAR_MPPT_VE_CAN_150_70(ProductType.MPPT, 0xA102, "SmartSolar MPPT VE.Can 150/70"),
    SMARTSOLAR_MPPT_VE_CAN_150_45(ProductType.MPPT, 0xA103, "SmartSolar MPPT VE.Can 150/45"),
    SMARTSOLAR_MPPT_VE_CAN_150_60(ProductType.MPPT, 0xA104, "SmartSolar MPPT VE.Can 150/60"),
    SMARTSOLAR_MPPT_VE_CAN_150_85(ProductType.MPPT, 0xA105, "SmartSolar MPPT VE.Can 150/85"),
    SMARTSOLAR_MPPT_VE_CAN_150_100(ProductType.MPPT, 0xA106, "SmartSolar MPPT VE.Can 150/100"),
    SMARTSOLAR_MPPT_VE_CAN_250_45(ProductType.MPPT, 0xA107, "SmartSolar MPPT VE.Can 250/45"),
    SMARTSOLAR_MPPT_VE_CAN_250_60(ProductType.MPPT, 0xA108, "SmartSolar MPPT VE.Can 250/60"),
    SMARTSOLAR_MPPT_VE_CAN_250_70(ProductType.MPPT, 0xA109, "SmartSolar MPPT VE.Can 250/70"),
    SMARTSOLAR_MPPT_VE_CAN_250_85(ProductType.MPPT, 0xA10A, "SmartSolar MPPT VE.Can 250/85"),
    SMARTSOLAR_MPPT_VE_CAN_250_100(ProductType.MPPT, 0xA10B, "SmartSolar MPPT VE.Can 250/100"),
    PHOENIX_INVERTER_12V_250VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA201, "Phoenix Inverter 12V 250VA 230V", true),
    PHOENIX_INVERTER_24V_250VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA202, "Phoenix Inverter 24V 250VA 230V", true),
    PHOENIX_INVERTER_48V_250VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA204, "Phoenix Inverter 48V 250VA 230V", true),
    PHOENIX_INVERTER_12V_375VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA211, "Phoenix Inverter 12V 375VA 230V", true),
    PHOENIX_INVERTER_24V_375VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA212, "Phoenix Inverter 24V 375VA 230V", true),
    PHOENIX_INVERTER_48V_375VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA214, "Phoenix Inverter 48V 375VA 230V", true),
    PHOENIX_INVERTER_12V_500VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA221, "Phoenix Inverter 12V 500VA 230V", true),
    PHOENIX_INVERTER_24V_500VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA222, "Phoenix Inverter 24V 500VA 230V", true),
    PHOENIX_INVERTER_48V_500VA_230V_DISCONTINUED(ProductType.INVERTER, 0xA224, "Phoenix Inverter 48V 500VA 230V", true),
    PHOENIX_INVERTER_12V_250VA_230V(ProductType.INVERTER, 0xA231, "Phoenix Inverter 12V 250VA 230V"),
    PHOENIX_INVERTER_24V_250VA_230V(ProductType.INVERTER, 0xA232, "Phoenix Inverter 24V 250VA 230V"),
    PHOENIX_INVERTER_48V_250VA_230V(ProductType.INVERTER, 0xA234, "Phoenix Inverter 48V 250VA 230V"),
    PHOENIX_INVERTER_12V_250VA_120V(ProductType.INVERTER, 0xA239, "Phoenix Inverter 12V 250VA 120V"),
    PHOENIX_INVERTER_24V_250VA_120V(ProductType.INVERTER, 0xA23A, "Phoenix Inverter 24V 250VA 120V"),
    PHOENIX_INVERTER_48V_250VA_120V(ProductType.INVERTER, 0xA23C, "Phoenix Inverter 48V 250VA 120V"),
    PHOENIX_INVERTER_12V_375VA_230V(ProductType.INVERTER, 0xA241, "Phoenix Inverter 12V 375VA 230V"),
    PHOENIX_INVERTER_24V_375VA_230V(ProductType.INVERTER, 0xA242, "Phoenix Inverter 24V 375VA 230V"),
    PHOENIX_INVERTER_48V_375VA_230V(ProductType.INVERTER, 0xA244, "Phoenix Inverter 48V 375VA 230V"),
    PHOENIX_INVERTER_12V_375VA_120V(ProductType.INVERTER, 0xA249, "Phoenix Inverter 12V 375VA 120V"),
    PHOENIX_INVERTER_24V_375VA_120V(ProductType.INVERTER, 0xA24A, "Phoenix Inverter 24V 375VA 120V"),
    PHOENIX_INVERTER_48V_375VA_120V(ProductType.INVERTER, 0xA24C, "Phoenix Inverter 48V 375VA 120V"),
    PHOENIX_INVERTER_12V_500VA_230V(ProductType.INVERTER, 0xA251, "Phoenix Inverter 12V 500VA 230V"),
    PHOENIX_INVERTER_24V_500VA_230V(ProductType.INVERTER, 0xA252, "Phoenix Inverter 24V 500VA 230V"),
    PHOENIX_INVERTER_48V_500VA_230V(ProductType.INVERTER, 0xA254, "Phoenix Inverter 48V 500VA 230V"),
    PHOENIX_INVERTER_12V_500VA_120V(ProductType.INVERTER, 0xA259, "Phoenix Inverter 12V 500VA 120V"),
    PHOENIX_INVERTER_24V_500VA_120V(ProductType.INVERTER, 0xA25A, "Phoenix Inverter 24V 500VA 120V"),
    PHOENIX_INVERTER_48V_500VA_120V(ProductType.INVERTER, 0xA25C, "Phoenix Inverter 48V 500VA 120V"),
    PHOENIX_INVERTER_12V_800VA_230V(ProductType.INVERTER, 0xA261, "Phoenix Inverter 12V 800VA 230V"),
    PHOENIX_INVERTER_24V_800VA_230V(ProductType.INVERTER, 0xA262, "Phoenix Inverter 24V 800VA 230V"),
    PHOENIX_INVERTER_48V_800VA_230V(ProductType.INVERTER, 0xA264, "Phoenix Inverter 48V 800VA 230V"),
    PHOENIX_INVERTER_12V_800VA_120V(ProductType.INVERTER, 0xA269, "Phoenix Inverter 12V 800VA 120V"),
    PHOENIX_INVERTER_24V_800VA_120V(ProductType.INVERTER, 0xA26A, "Phoenix Inverter 24V 800VA 120V"),
    PHOENIX_INVERTER_48V_800VA_120V(ProductType.INVERTER, 0xA26C, "Phoenix Inverter 48V 800VA 120V"),
    PHOENIX_INVERTER_12V_1200VA_230V(ProductType.INVERTER, 0xA271, "Phoenix Inverter 12V 1200VA 230V"),
    PHOENIX_INVERTER_24V_1200VA_230V(ProductType.INVERTER, 0xA272, "Phoenix Inverter 24V 1200VA 230V"),
    PHOENIX_INVERTER_48V_1200VA_230V(ProductType.INVERTER, 0xA274, "Phoenix Inverter 48V 1200VA 230V"),
    PHOENIX_INVERTER_12V_1200VA_120V(ProductType.INVERTER, 0xA279, "Phoenix Inverter 12V 1200VA 120V"),
    PHOENIX_INVERTER_24V_1200VA_120V(ProductType.INVERTER, 0xA27A, "Phoenix Inverter 24V 1200VA 120V"),
    PHOENIX_INVERTER_48V_1200VA_120V(ProductType.INVERTER, 0xA27C, "Phoenix Inverter 48V 1200VA 120V"),
    PHOENIX_INVERTER_12V_1600VA_230V(ProductType.INVERTER, 0xA281, "Phoenix Inverter 12V 1600VA 230V"),
    PHOENIX_INVERTER_24V_1600VA_230V(ProductType.INVERTER, 0xA282, "Phoenix Inverter 24V 1600VA 230V"),
    PHOENIX_INVERTER_48V_1600VA_230V(ProductType.INVERTER, 0xA284, "Phoenix Inverter 48V 1600VA 230V"),
    PHOENIX_INVERTER_12V_2000VA_230V(ProductType.INVERTER, 0xA291, "Phoenix Inverter 12V 2000VA 230V"),
    PHOENIX_INVERTER_24V_2000VA_230V(ProductType.INVERTER, 0xA292, "Phoenix Inverter 24V 2000VA 230V"),
    PHOENIX_INVERTER_48V_2000VA_230V(ProductType.INVERTER, 0xA294, "Phoenix Inverter 48V 2000VA 230V"),
    PHOENIX_INVERTER_12V_3000VA_230V(ProductType.INVERTER, 0xA2A1, "Phoenix Inverter 12V 3000VA 230V"),
    PHOENIX_INVERTER_24V_3000VA_230V(ProductType.INVERTER, 0xA2A2, "Phoenix Inverter 24V 3000VA 230V"),
    PHOENIX_INVERTER_48V_3000VA_230V(ProductType.INVERTER, 0xA2A4, "Phoenix Inverter 48V 3000VA 230V"),
    PHOENIX_SMART_IP43_CHARGER_12_50_1_1(ProductType.CHARGER, 0xA340, "Phoenix Smart IP43 Charger 12|50 (1+1)"),
    PHOENIX_SMART_IP43_CHARGER_12_50_3(ProductType.CHARGER, 0xA341, "Phoenix Smart IP43 Charger 12|50 (3)"),
    PHOENIX_SMART_IP43_CHARGER_24_25_1_1(ProductType.CHARGER, 0xA342, "Phoenix Smart IP43 Charger 24|25 (1+1)"),
    PHOENIX_SMART_IP43_CHARGER_24_25_3(ProductType.CHARGER, 0xA343, "Phoenix Smart IP43 Charger 24|25 (3)"),
    PHOENIX_SMART_IP43_CHARGER_12_30_1_1(ProductType.CHARGER, 0xA344, "Phoenix Smart IP43 Charger 12|30 (1+1)"),
    PHOENIX_SMART_IP43_CHARGER_12_30_3(ProductType.CHARGER, 0xA345, "Phoenix Smart IP43 Charger 12|30 (3)"),
    PHOENIX_SMART_IP43_CHARGER_24_16_1_1(ProductType.CHARGER, 0xA346, "Phoenix Smart IP43 Charger 24|16 (1+1)"),
    PHOENIX_SMART_IP43_CHARGER_24_16_3(ProductType.CHARGER, 0xA347, "Phoenix Smart IP43 Charger 24|16 (3)"),
    UNKNOWN(ProductType.UNKNOWN,0xFFFFF, "Unknown")
    ;

    private final ProductType productType;
    private final int productId;
    private final String productName;
    private final boolean discontinued;

    VictronProduct(ProductType productType, int productId, String productName, boolean discontinued) {
        this.productType = productType;
        this.productId = productId;
        this.productName = productName;
        this.discontinued = discontinued;
    }

    VictronProduct(ProductType productType, int productId, String productName) {
        this(productType, productId, productName, false);
    }

    /**
     * @return The type of product of the Victron Energy Device.
     */
    public ProductType getProductType() {
        return productType;
    }

    /**
     * @return The integer ProductId of the Victron Energy Device.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * @return The String hexadecimal value of this product id.
     */
    public String getProductIdHex() {
        return String.format("0x%08X", productId & 0xFFFFFF);
    }

    /**
     * @return The Product's Name.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Static method to get all known VictronProducts.
     * @return A Stream of matching VictronProducts.
     */
    public static Stream<VictronProduct> all() {
        return Stream.of(VictronProduct.values());
    }

    /**
     * Static method to match known VictronProducts by Predicate.
     * @param predicate
     * @return A Stream of matching VictronProducts.
     */
    public static Stream<VictronProduct> by(Predicate<VictronProduct> predicate) {
        return all()
                .filter(predicate);
    }

    /**
     * Static method that searches the enum for matching VictronProducts in a fluent way.
     * @param getter The Field to compare.
     * @param operator The comparison algorithm.
     * @param match The value to compare.
     * @param <T> The type of Field/Value compared.
     * @return A Stream of matching VictronProducts.
     */
    public static <T> Stream<VictronProduct> by(Function<VictronProduct, T> getter, BiPredicate<T, T> operator, T match) {
        return by(victronProduct -> operator.test(getter.apply(victronProduct), match));
    }

    /**
     * Static method to get a VictronProduct by ProductId, if known.
     * @param hexProductId The String-representation hexadecimal of the productId.
     * @return An Optional VictronProduct.
     */
    public static Optional<VictronProduct> byProductId(String hexProductId) {
        return byProductId(
                Integer.parseInt(hexProductId.startsWith("0x") ?
                                hexProductId.substring(2) : hexProductId
                , 16));
    }

    /**
     * Static method to get a VictronProduct by its ProductId, if known.
     * @param productId The integer/hex literal productId.
     * @return An Optional VictronProduct.
     */
    public static Optional<VictronProduct> byProductId(int productId) {
        return by(VictronProduct::getProductId, Objects::equals, productId)
                .findFirst();
    }

    /**
     * Static method to get VictronProducts by ProductType..
     * @param productType product type enum.
     * @return Stream of matching VictronProducts.
     */
    public static Stream<VictronProduct> byProductId(ProductType productType) {
        return by(VictronProduct::getProductType, Objects::equals, productType);
    }
}
