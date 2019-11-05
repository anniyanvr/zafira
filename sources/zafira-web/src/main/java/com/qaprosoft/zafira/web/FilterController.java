package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.Filter;
import com.qaprosoft.zafira.models.dto.filter.FilterDTO;
import com.qaprosoft.zafira.models.dto.filter.Subject;
import com.qaprosoft.zafira.service.FilterService;
import com.qaprosoft.zafira.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api("Filters API")
@CrossOrigin
@RequestMapping(path = "api/filters", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FilterController extends AbstractController {

    private final FilterService filterService;
    private final Mapper mapper;

    public FilterController(FilterService filterService, Mapper mapper) {
        this.filterService = filterService;
        this.mapper = mapper;
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Create filter", nickname = "createFilter", httpMethod = "POST", response = FilterDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PostMapping()
    public FilterDTO createFilter(@RequestBody @Valid FilterDTO filterDTO) {
        filterDTO.getSubject().sortCriterias();
        return mapper.map(filterService.createFilter(mapper.map(filterDTO, Filter.class), getPrincipalId(), isAdmin()), FilterDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get all public filters", nickname = "getAllPublicFilters", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/all/public")
    public List<FilterDTO> getAllPublicFilters() {
        List<Filter> publicFilters = filterService.getAllPublicFilters(getPrincipalId());
        return publicFilters.stream()
                            .map(filter -> mapper.map(filter, FilterDTO.class))
                            .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Update filter", nickname = "updateFilter", httpMethod = "PUT", response = FilterDTO.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PutMapping()
    public FilterDTO updateFilter(@RequestBody @Valid FilterDTO filterDTO) {
        filterDTO.getSubject().sortCriterias();
        return mapper.map(filterService.updateFilter(mapper.map(filterDTO, Filter.class), getPrincipalId(), isAdmin()), FilterDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Delete filter", nickname = "deleteFilter", httpMethod = "DELETE")
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @DeleteMapping("/{id}")
    public void deleteFilter(@PathVariable("id") Long id) {
        filterService.deleteFilterById(id, getPrincipalId());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Get filter builder", nickname = "getBuilder", httpMethod = "GET", response = Subject.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @GetMapping("/{name}/builder")
    public Subject getBuilder(@PathVariable("name") Subject.Name name) {
        return filterService.getStoredSubject(name);
    }

}
