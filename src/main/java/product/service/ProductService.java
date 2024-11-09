package product.service;

import static global.utils.StringParser.parseInt;

import global.constants.FileType;
import global.utils.FileParser;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import product.constants.ProductPresetKeys;
import product.entity.Product;
import product.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void setUp(String filePath) {
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

        Product product = new Product(name, price, quantity, promotionName);
        productRepository.save(product);
    }

    public Product getByName(String name) {
        Optional<Product> maybeProduct = productRepository.findByName(name);
        if (maybeProduct.isEmpty()) {
            //FIXME: 예외 유형 확인
            throw new IllegalArgumentException("해당 이름을 가진 제품을 찾을 수 없습니다");
        }
        return maybeProduct.get();
    }
}
