package com.myuser.ugcampusroutefinder.model;

import java.util.List;

public record Path(List<Location> locations, double totalWeight) {
}
