package com.web.CoursesQuiz.packages.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties.Packages;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.web.CoursesQuiz.packages.entity.Pkg;
import com.web.CoursesQuiz.packages.service.PackagesService;

@RestController
public class PackagesController {

    @Autowired
    private PackagesService packagesService;

    @GetMapping("/packages")
    public List<Pkg> getPackages() {
        return packagesService.getPackages();
    }

    @GetMapping("/packages/{id}")
    public Pkg getPackage(@PathVariable String id) {
        return packagesService.getPackage(id);
    }

    @PostMapping("/package")
    public void addPackage(@RequestBody Pkg pkg) {
        packagesService.addPackage(pkg);
    }

    @PutMapping("/packages/{id}")
    public void updatePackage(@RequestBody Pkg packages, @PathVariable String id) {
        packagesService.updatePackage(id, packages);
    }

    @DeleteMapping("/packages/{id}")
    public void deletePackage(@PathVariable String id) {
        packagesService.deletePackage(id);
    }
}
