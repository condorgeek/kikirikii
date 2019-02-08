/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PageRepository.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 08.02.19 20:14
 */

package com.kikirikii.repos;

import com.kikirikii.model.Page;
import org.springframework.data.repository.CrudRepository;

public interface PageRepository extends CrudRepository<Page, Long> {
}
