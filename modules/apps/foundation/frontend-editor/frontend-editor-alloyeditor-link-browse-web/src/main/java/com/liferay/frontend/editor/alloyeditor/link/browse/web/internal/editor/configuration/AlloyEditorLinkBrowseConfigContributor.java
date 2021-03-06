/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.frontend.editor.alloyeditor.link.browse.web.internal.editor.configuration;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.portal.kernel.editor.configuration.BaseEditorConfigContributor;
import com.liferay.portal.kernel.editor.configuration.EditorConfigContributor;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(
	property = {
		"editor.name=alloyeditor", "editor.name=alloyeditor_bbcode",
		"editor.name=alloyeditor_creole", "service.ranking:Integer=1000"
	},
	service = EditorConfigContributor.class
)
public class AlloyEditorLinkBrowseConfigContributor
	extends BaseEditorConfigContributor {

	@Override
	public void populateConfigJSONObject(
		JSONObject jsonObject, Map<String, Object> inputEditorTaglibAttributes,
		ThemeDisplay themeDisplay,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory) {

		JSONObject toolbarsJSONObject = jsonObject.getJSONObject("toolbars");

		if (toolbarsJSONObject == null) {
			toolbarsJSONObject = JSONFactoryUtil.createJSONObject();
		}

		JSONObject stylesJSONObject = toolbarsJSONObject.getJSONObject(
			"styles");

		if (stylesJSONObject == null) {
			stylesJSONObject = JSONFactoryUtil.createJSONObject();
		}

		JSONArray selectionsJSONArray = stylesJSONObject.getJSONArray(
			"selections");

		if (selectionsJSONArray != null) {
			for (int i = 0; i < selectionsJSONArray.length(); i++) {
				JSONObject selectionJSONObject =
					selectionsJSONArray.getJSONObject(i);

				String name = selectionJSONObject.getString("name");

				if (name.equals("text") || name.equals("link")) {
					JSONArray buttonsJSONArray =
						selectionJSONObject.getJSONArray("buttons");

					selectionJSONObject.put(
						"buttons", updateButtonsJSONArray(buttonsJSONArray));
				}
			}

			stylesJSONObject.put("selections", selectionsJSONArray);
		}

		toolbarsJSONObject.put("styles", stylesJSONObject);

		jsonObject.put("toolbars", toolbarsJSONObject);

		String namespace = GetterUtil.getString(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:namespace"));

		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));

		populateFileBrowserURL(
			jsonObject, requestBackedPortletURLFactory,
			namespace + name + "selectDocument");
	}

	protected void populateFileBrowserURL(
		JSONObject jsonObject,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		String eventName) {

		List<ItemSelectorReturnType> desiredItemSelectorReturnTypes =
			new ArrayList<>();

		desiredItemSelectorReturnTypes.add(new URLItemSelectorReturnType());

		ItemSelectorCriterion itemSelectorCriterion =
			new FileItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			desiredItemSelectorReturnTypes);

		PortletURL itemSelectorURL = _itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, eventName, itemSelectorCriterion);

		jsonObject.put("documentBrowseLinkUrl", itemSelectorURL.toString());
	}

	@Reference(unbind = "-")
	protected void setItemSelector(ItemSelector itemSelector) {
		_itemSelector = itemSelector;
	}

	protected JSONArray updateButtonsJSONArray(JSONArray oldButtonsJSONArray) {
		JSONArray newButtonsJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < oldButtonsJSONArray.length(); i++) {
			JSONObject oldButtonJSONObject = oldButtonsJSONArray.getJSONObject(
				i);

			if (oldButtonJSONObject == null) {
				String buttonName = oldButtonsJSONArray.getString(i);

				if (buttonName.equals("link")) {
					newButtonsJSONArray.put("linkBrowse");
				}
				else if (buttonName.equals("linkEdit")) {
					newButtonsJSONArray.put("linkEditBrowse");
				}
				else {
					newButtonsJSONArray.put(buttonName);
				}
			}
			else {
				newButtonsJSONArray.put(oldButtonJSONObject);
			}
		}

		return newButtonsJSONArray;
	}

	private ItemSelector _itemSelector;

}