package org.huel.cloudhub.client.disk.controller.storage;

import org.huel.cloudhub.client.disk.controller.Api;
import org.huel.cloudhub.client.disk.controller.ParameterHelper;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchCondition;
import org.huel.cloudhub.client.disk.domain.storagesearch.SearchExpressionParser;
import org.huel.cloudhub.client.disk.domain.storagesearch.StorageSearchService;
import org.huel.cloudhub.client.disk.domain.userstorage.AttributedStorage;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageOwner;
import org.huel.cloudhub.client.disk.controller.storage.vo.StorageVo;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class StorageSearchController {
    private final StorageSearchService storageSearchService;
    private final SearchExpressionParser searchExpressionParser;

    public StorageSearchController(StorageSearchService storageSearchService,
                                   SearchExpressionParser searchExpressionParser) {
        this.storageSearchService = storageSearchService;
        this.searchExpressionParser = searchExpressionParser;
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/search")
    public HttpResponseEntity<List<StorageVo>> searchStorages(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String type,
            @RequestParam("expr") String expression) {
        StorageOwner storageOwner = ParameterHelper.buildStorageOwner(ownerId, type);
        List<SearchCondition> searchConditions =
                searchExpressionParser.parse(expression);
        List<? extends AttributedStorage> attributedStorages =
                storageSearchService.searchFor(searchConditions, storageOwner);
        return HttpResponseEntity.success(
                attributedStorages.stream().map(StorageVo::from).toList()
        );
    }
}
