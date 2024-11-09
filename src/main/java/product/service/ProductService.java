package product.service;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;
import static global.utils.StringParser.parseInt;

import global.constants.FileType;
import global.utils.FileParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import product.constants.ProductPresetKeys;
import product.dto.response.ProductInfo;
import product.dto.response.PurchaseInfo;
import product.entity.Product;
import product.repository.ProductRepository;
import promotion.entity.Promotion;
import promotion.service.PromotionService;
import store.dto.request.PurchaseRequest;

public class ProductService {

    private final PromotionService promotionService;
    private final ProductRepository productRepository;

    public ProductService(PromotionService promotionService, ProductRepository productRepository) {
        this.promotionService = promotionService;
        this.productRepository = productRepository;
    }

    public void loadFromFilePath(String filePath) {
        List<Map<String, String>> productFileDataSets = FileParser.parsingByFilePath(filePath, FileType.PRODUCT);
        for (Map<String, String> dataSet : productFileDataSets) {
            createByDataSet(dataSet);
        }
    }

    private void createByDataSet(Map<String, String> dataSet) {
        String name = dataSet.get(ProductPresetKeys.PRODUCT_NAME_PRESET_KEY.getKey());
        int price = parseInt(dataSet.get(ProductPresetKeys.PRODUCT_PRICE_PRESET_KEY.getKey()));
        int quantity = parseInt(dataSet.get(ProductPresetKeys.PRODUCT_QUANTITY_PRESET_KEY.getKey()));
        String promotionName = dataSet.get(ProductPresetKeys.PRODUCT_PROMOTION_NAME_PRESET_KEY.getKey());
        Promotion promotionByName = getPromotionByName(promotionName);

        Product product = new Product(name, price, quantity, promotionByName);
        productRepository.save(product);
    }

    public Product getByName(String name) {
        Optional<Product> maybeProduct = productRepository.findByName(name);
        if (maybeProduct.isEmpty()) {
            throw new IllegalArgumentException(ERROR_MSG_PREFIX + "해당 이름을 가진 제품을 찾을 수 없습니다 : " + name);
        }
        return maybeProduct.get();
    }

    private Promotion getPromotionByName(String promotionName) {
        Promotion promotion;
        try {
            promotion = promotionService.getByName(promotionName);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return promotion;
    }

    public List<ProductInfo> getProductInfos() {
        List<Product> products = getAll();
        List<ProductInfo> productInfos = new ArrayList<>();
        for (Product product : products) {
            productInfos.add(ProductInfo.fromProduct(product));
        }
        return productInfos;
    }

    private List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getAllByName(String name) {
        List<Product> products = productRepository.findAllByName(name);
        if (products.isEmpty()) {
            throw new IllegalArgumentException(ERROR_MSG_PREFIX + "해당 이름을 가진 제품을 찾을 수 없습니다" + name);
        }
        return new ArrayList<>(products);
    }

    public int getQuantityByName(String name) {
        return productRepository.countAllByName(name);
    }

//
//    public void modifyQuantities(List<Product> targetProducts, int purchaseQuantity) {
//        Product target = targetProducts.remove(0);
//
//        if (target.getQuantity() >= purchaseQuantity) {
//            target.subtractQuantity(purchaseQuantity);
//            return;
//        }
//
//        while (purchaseQuantity != 0) {
//            int currentQuantity = Math.min(target.getQuantity(), purchaseQuantity);
//            target.subtractQuantity(currentQuantity);
//            purchaseQuantity -= currentQuantity;
//
//            if (!targetProducts.isEmpty()) {
//                target = targetProducts.remove(0);
//            }
//        }
//    }

    public List<PurchaseInfo> getPurchaseInfos(List<PurchaseRequest> requests, List<Promotion> promotions) {
        List<PurchaseInfo> purchaseInfos = new ArrayList<>();
        for (PurchaseRequest request : requests) {
            Optional<Product> maybeProduct = productRepository.findByNameAndHasPromotion(request.productName());
            if (maybeProduct.isPresent() && promotions.contains(maybeProduct.get().getPromotion())) {
                purchaseInfos.add(getPurchaseInfo(request, maybeProduct.get()));
                continue;
            }
            purchaseInfos.add(getPurchaseInfo(request, getByName(request.productName())));
        }
        return purchaseInfos;
    }

    private PurchaseInfo getPurchaseInfo(PurchaseRequest request, Product product) {
        int remainingQuantity = product.getQuantity();
        int purchaseQuantity = request.quantity();

        if (remainingQuantity < purchaseQuantity) {
            return new PurchaseInfo(product, purchaseQuantity - remainingQuantity, remainingQuantity);
        }

        return new PurchaseInfo(product, 0, purchaseQuantity);
    }

    public void purchase(PurchaseInfo info) {
        if(info.bonusPurchaseQuantity() != 0) {
            info.product().subtractQuantity(info.bonusPurchaseQuantity());
        }
        if(info.normalPurchaseQuantity() != 0 && info.product().getPromotion() != null) {
            Product product = productRepository.findByNameAndNotHasPromotion(info.product().getName()).get();
            product.subtractQuantity(info.normalPurchaseQuantity());
        }
        if(info.normalPurchaseQuantity() != 0 && info.product().getPromotion() == null) {
            info.product().subtractQuantity(info.normalPurchaseQuantity());
        }
    }
}
