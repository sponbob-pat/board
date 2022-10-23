package org.example.board.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.board.service.posts.PostsService;
import org.example.board.web.dto.PostsListResponseDto;
import org.example.board.web.dto.PostsResponseDto;
import org.example.board.web.dto.PostsSearchCriteriaDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// index.mustache 파일에 url을 매핑하기 위해서 IndexController라는 클래스 설정
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model,
    @PageableDefault(size = 3,  sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // posts 속성의 값을 index.mustache에 전달
        model.addAttribute("posts", postsService.findAllPage(pageable));

        return "index"; // 이렇게 문자열을 반환하면 .mustache 파일이 View Resolver에 의해 처리됨
    }

    @GetMapping("/sort")
    public String sortingIndex(@RequestParam String column,
                               @RequestParam String sort, Model model) {

        // pageable에서 기본적으로 사용하는 파라미터 size, sot, direction

        // 전달받은 column을 기준으로 오름차순해서 보낸다.

        // 오름차순일 때
        if(sort.equals("asc")) model.addAttribute("posts", postsService.findAllAsc(column));

        // 내림차순일 때
        else if(sort.equals("desc")) model.addAttribute("posts", postsService.findAllDesc(column));

        return "index";
    }

    // /posts/save라는 url로 이동하면
    // posts-save.mustache 라는 파일로 이동할 거다.
    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }

    @GetMapping("/posts/delete")
    public String postsSeveralDelete(@RequestParam List<Long> checkedItem){

        for(Long id : checkedItem) {
            postsService.delete(id);
        }

        // '/'을 전달해야 index.mustache 파일이 화면에 출력되니까
        // redirect:/ 를 return하도록 했다.
        return "redirect:/";

    }

    @GetMapping("/posts/search")
    public String postsSearch(@RequestParam String keyword, Model model,
                              @PageableDefault(size = 3) Pageable pageable) {

        String[] columns = postsService.getColumnName();

        SearchSpecification searchSpecification = new SearchSpecification((new PostsSearchCriteriaDto(columns, keyword)));

        Page<PostsListResponseDto> pages = postsService.findAllPage(searchSpecification, pageable);

        model.addAttribute("posts", pages);

        return "index";
    }



}
