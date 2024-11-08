package product.service;

import static global.utils.StringParser.parseInt;
import static product.constants.ProductStatic.getProductPresetKeys;

import global.dto.response.FileParsedResponse;
import global.utils.FileParser;
import java.util.List;
import java.util.Optional;
import product.entity.Product;
import product.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public void setUp(String filePath) {
        FileParsedResponse productData = FileParser.parsingByFilePath(filePath);
        productKeysValidate(productData.keys());

        List<List<String>> productValues = productData.values();
        for (List<String> value : productValues) {
            create(value);
        }
    }

    private void productKeysValidate(List<String> keys) {
        List<String> productPresetKeys = getProductPresetKeys();
        keysCountValidate(keys.size(), productPresetKeys.size());
        keysContainsValidate(keys, productPresetKeys);
        keysOrderValidate(keys, productPresetKeys);
    }

    private void keysCountValidate(int keyCount, int presetKeyCount) {
        if (keyCount != presetKeyCount) {
            throw new IllegalArgumentException("파일의 키 값이 %d개 여야 합니다".formatted(presetKeyCount));
        }
    }

    private void keysContainsValidate(List<String> keys, List<String> presetKeys) {
        for (String presetKey : presetKeys) {
            if (!keys.contains(presetKey)) {
                throw new IllegalArgumentException("파일에서 사전 설정 키 값을 찾을 수 없습니다" + presetKey);
            }
        }
    }

    private void keysOrderValidate(List<String> keys, List<String> presetKeys) {
        for (int i = 0; i < keys.size(); i++) {
            if (!keys.get(i).equals(presetKeys.get(i))) {
                throw new IllegalArgumentException("파일의 데이터 키 순서가 일치하지 않습니다");
            }
        }
    }

    private void create(List<String> value) {
        Product product = new Product(value.get(0), parseInt(value.get(1)), parseInt(value.get(2)), value.get(3));
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
