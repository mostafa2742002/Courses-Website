package com.web.CoursesQuiz.packages.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.web.CoursesQuiz.packages.entity.Pkg;
import com.web.CoursesQuiz.packages.repo.PkgRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PackagesService {

    private final PkgRepository pkgRepository;

    public List<Pkg> getPackages() {
        return pkgRepository.findAll();
    }

    public Pkg getPackage(String id) {
        if (pkgRepository.findById(id).isPresent()) {
            return pkgRepository.findById(id).get();
        } else {
            throw new RuntimeException("Package not found for id: " + id);
        }
    }

    public void addPackage(Pkg pkg) {
        if (pkgRepository.findByName(pkg.getName()) != null) {
            throw new RuntimeException("Package already exists with name: " + pkg.getName());
        }
        pkg.setId(null);
        pkgRepository.save(pkg);
    }

    public void updatePackage(String id, Pkg packages) {
        if (pkgRepository.findById(id).isPresent()) {
            Pkg pkg = pkgRepository.findById(id).get();
            pkg.setName(packages.getName());
            pkg.setDescription(packages.getDescription());
            pkg.setPriceForEgypt(packages.getPriceForEgypt());
            pkg.setPriceForNonEgypt(packages.getPriceForNonEgypt());
            pkgRepository.save(pkg);
        } else {
            throw new RuntimeException("Package not found for id: " + id);
        }
    }

    public void deletePackage(String id) {
        if (pkgRepository.findById(id).isPresent()) {
            pkgRepository.deleteById(id);
        } else {
            throw new RuntimeException("Package not found for id: " + id);
        }
    }

}
