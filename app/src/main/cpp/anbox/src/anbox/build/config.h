/*
 * Copyright (C) 2016 Canonical, Ltd.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Authored by: Thomas Voß <thomas.voss@canonical.com>
 *
 */

#ifndef ANBOX_CONFIG_H_
#define ANBOX_CONFIG_H_

#include <cstdint>
#include <string>

namespace anbox::build {
/// @brief version marks the version
static constexpr const char *version{"0.0.2"};

// path for system configuration
static constexpr const char *default_resource_path{""};
static constexpr const char *default_data_path{""};
}

#endif  // ANBOX_CONFIG_H_
