package org.huel.cloudhub.client.disk.controller.tag;

import org.huel.cloudhub.client.disk.controller.AdminApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author RollW
 */
@AdminApi
public class ContentTagController {

    @GetMapping("/tags")
    public void getTags() {
    }

    @GetMapping("/tags/{tagId}")
    public void getTag() {
    }

    @GetMapping("/tags/groups")
    public void getTagGroups() {
    }

    @GetMapping("/tags/groups/{groupId}")
    public void getTagGroup() {
    }

    @PostMapping("/tags/groups/{groupId}/infile")
    public void importTags() {
    }

    @GetMapping("/tags/groups/{groupId}/infile")
    public void exportTags() {
    }
}
