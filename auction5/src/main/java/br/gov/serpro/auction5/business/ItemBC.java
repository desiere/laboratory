/*
 * Demoiselle Framework
 * Copyright (c) 2009 Serpro and other contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of contributors.
 *
 * Demoiselle Framework is an open source Java EE library designed to accelerate
 * the development of transactional database Web applications.
 *
 * Demoiselle Framework is released under the terms of the LGPL license 3
 * http://www.gnu.org/licenses/lgpl.html  LGPL License 3
 *
 * This file is part of Demoiselle Framework.
 *
 * Demoiselle Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License 3 as published by
 * the Free Software Foundation.
 *
 * Demoiselle Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Demoiselle Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.gov.serpro.auction5.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.util.ResourceBundle;
import br.gov.serpro.auction5.domain.Auction;
import br.gov.serpro.auction5.domain.Category;
import br.gov.serpro.auction5.domain.Item;
import br.gov.serpro.auction5.exception.ApplicationRuntimeException;
import br.gov.serpro.auction5.persistence.AuctionDAO;
import br.gov.serpro.auction5.persistence.CategoryDAO;
import br.gov.serpro.auction5.persistence.ItemDAO;


/**
 * @author CETEC/CTJEE
 * @see IBusinessController
 */
public class ItemBC implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ResourceBundle bundle;

	@Inject
	private ItemDAO itemDAO;

	@Inject
	private CategoryDAO categoryDAO;

	@Inject
	AuctionDAO auctionDAO;

	public void delete(Item item) {
		try {
			item = itemDAO.load(item.getId());
			if (item == null) {
				throw new ApplicationRuntimeException(bundle.getString("OBJECT_NOT_FOUND"));
			}
			List<Auction> listAuctions = auctionDAO.listAllAuctionsByItem(item);
			if (listAuctions != null)
				if (listAuctions.size() > 0)
					throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_DELETE_NOK_THERE_ARE_AUCTIONS"));
			itemDAO.delete(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_DELETED_NOK" + e));
		}
	}

	public List<Item> filterByCategory(Item item) {
		if (item.getCategory() != null) {
			try {
				List<Item> list = itemDAO.listByCategory(item.getCategory());
				return list;
			} catch (Exception e) {
				throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_LIST_LOAD_NOK", e));
			}
		}
		return new ArrayList<Item>();
	}

	private void validateItem(Item item) {
		if (item.getCategory() == null || item.getCategory().getId() == null) {
			throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_VALIDATE_CATEGORY_NOK"));
		}
		if (item.getDescription() == null
				|| item.getDescription().length() == 0) {
			throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_VALIDATE_DESCRIPTION_NOK"));
		}
	}

	public Item save(Item item) {
		validateItem(item);
		try {
			if (item.getId() != null) {
				itemDAO.update(item);
			} else {
				itemDAO.insert(item);
			}
		} catch (Exception e) {
			throw new ApplicationRuntimeException(bundle.getString("ADM_ITEM_SAVE_NOK", e));
		}
		return item;
	}

	public List<Category> listAvailableCategories() {
		try {
			return categoryDAO.listAvailableCategories();
		} catch (Exception e) {
			throw new ApplicationRuntimeException(bundle.getString("LIST_CATEGORY_NOK", e));
		}
	}

}