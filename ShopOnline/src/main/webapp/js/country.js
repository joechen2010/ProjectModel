
Ext.onReady(function(){
	var myPageSize = 10;
	
	var validFlagArray = [['Y','有效'],['N','无效']];
	
	var fieldName = "";
	
    // shorthand alias
    var fm = Ext.form, Ed = Ext.grid.GridEditor;
    
    //we enable the QuickTips for the later Pagebar
    Ext.QuickTips.init();
    
   /************************************************************
	* Display the result in page
    * the column model has information about grid columns
    * dataIndex maps the column to the specific data field in
    * the data store (created below)
    ************************************************************/
    var cm = new Ext.grid.ColumnModel([{
           id: 'id',
           header: "Identify",
           dataIndex: 'id',
           width: 50,
           hidden: true
        },{
           header: "国别名称",
           dataIndex: 'countryName',
           width: 170
        },{
           header: "国别代码",
           dataIndex: 'countryCode',
           width: 70
        },{
           header: "有效标记",
           dataIndex: 'validFlag',
           width: 70
        },{
           header: "创建者",
           dataIndex: 'createUser',
           width: 100
        },{
           header: "创建时间",
           dataIndex: 'createDate',
           width: 110
        },{
           header: "修改者",
           dataIndex: 'updateUser',
           width: 100
        },{
           header: "修改时间",
           dataIndex: 'updateDate',
           width: 110
        }
       ]);
      

    // by default columns are sortable
    cm.defaultSortable = true;

   /************************************************************
    * connect to backend - grid - core part
    * create the Data Store
    *   connect with backend and list the result in page
    *   through JSON format
    ************************************************************/
    var ds = new Ext.data.Store({
        // load using script tags for cross domain, if the data in on the same domain as
        // this page, an HttpProxy would be better
        proxy: new Ext.data.ScriptTagProxy({
		   url: window.location.pathname + "../country_listOrDelete"
        }),
        // create reader that reads the Topic records
        reader: new Ext.data.JsonReader({
            root: 'results',
            totalProperty: 'totalCount',
            id: 'id'
        }, [
            {name: 'id'},
            {name: 'countryName'},
			{name: 'countryCode'},	
			{name: 'validFlag'},
			{name: 'createUser'},
			{name: 'createDate'},
			{name: 'updateUser'},
			{name: 'updateDate'}
        ]),

        // turn on remote sorting
        remoteSort: true
    });

    ds.setDefaultSort('createDate', 'ASC');

    // create the editor grid
    var grid = new Ext.grid.EditorGrid('editor-grid', {
        ds: ds,
        cm: cm,
        //selModel: new Ext.grid.CellSelectionModel(),
        selModel: new Ext.grid.RowSelectionModel({singleSelect:false}),
        enableColLock:false
    });

    var layout = Ext.BorderLayout.create({
        center: {
            margins:{left:2,top:3,right:2,bottom:3},
            panels: [new Ext.GridPanel(grid)]
        }
    }, 'grid-panel');

    // render it
    grid.render();
 
   /************************************************************
    * create header panel 
    * add filter field - search function
    ************************************************************/
	var gridHead = grid.getView().getHeaderPanel(true);
	var tb = new Ext.Toolbar(gridHead);

	filterButton = new Ext.Toolbar.MenuButton({
	    icon: 'public/image/list-items.gif',
	    cls: 'x-btn-text-icon',
		text: '选择过滤器',
		tooltip: 'Select one of filter',
		menu: {
			items: [
				new Ext.menu.CheckItem({ text: '国别名称', value: 'countryName', checked: false, group: 'filter', checkHandler: onItemCheck }),
				new Ext.menu.CheckItem({ text: '国别代码', value: 'countryCode', checked: false, group: 'filter', checkHandler: onItemCheck }),
				new Ext.menu.CheckItem({ text: '有效标记', value: 'validFlag', checked: false, group: 'filter', checkHandler: onItemCheck })	
			]},
		minWidth: 105
	});
	tb.add(filterButton);

	// Create the filter field
	var filter = Ext.get(tb.addDom({ // add a DomHelper config to the toolbar and return a reference to it
	     tag: 'input', 
	     type: 'text', 
	     size: '30', 
	     value: '', 
		 style: 'background: #F0F0F9;'
	}).el);			

    // press enter keyboard
    filter.on('keypress', function(e) { // setup an onkeypress event handler
      if(e.getKey() == e.ENTER && this.getValue().length > 0) {// listen for the ENTER key
          ds.load({params:{start:0, limit:myPageSize}});
      }
    });
    
    filter.on('keyup', function(e) { // setup an onkeyup event handler
      if(e.getKey() == e.BACKSPACE && this.getValue().length === 0) {// listen for the BACKSPACE key and the field being empty
		  ds.load({params:{start:0, limit:myPageSize}});
      }
    });
 
    // Update search button text with selected item
	function onItemCheck(item, checked)
	{
		if(checked) {
			filterButton.setText(item.text + ':');
			fieldName = item.value;
		}
	}

   /************************************************************
    * create footer panel 
    *    actions and paging
    ************************************************************/ 
    var gridFoot = grid.getView().getFooterPanel(true);

    // add a paging toolbar to the grid's footer
    var paging = new Ext.PagingToolbar(gridFoot, ds, {
        pageSize: myPageSize,
        displayInfo: true,
        displayMsg: '共 {2} 项. 当前显示第 {0} - {1}项',
        emptyMsg: "没有记录"
    });
    // add the detailed add button
    paging.add('-', {
        pressed: true,
        enableToggle:true,
        text: '新增',
        cls: '',
        toggleHandler: doAdd
    });    
    // add the detailed delete button
    paging.add('-', {
        pressed: true,
        enableToggle:true,
        text: '删除',
        cls: '',
        toggleHandler: doDel
    });  
    // --- end -- create foot panel   

   /************************************************************
    * load parameter to backend 
    *    have beforelaod function
    ************************************************************/
	ds.on('beforeload', function() {
	  ds.baseParams = { // modify the baseParams setting for this request
	    filterValue: filter.getValue(),// retrieve the value of the filter input and assign it to a property named filter 
	    filterTxt: filterButton.getText(),
		fieldName: fieldName
	  };
	});
    // trigger the data store load
    ds.load({params:{start:0, limit:myPageSize}});

   /************************************************************
    * Action - delete
    *   start to handle delete function
    *   need confirm to delete
    ************************************************************/	
    function doDel(){
        var m = grid.getSelections();
        if(m.length > 0)
        {
        	Ext.MessageBox.confirm('确认', '确认要删除吗?' , doDel2);	
        }
        else
        {
        	Ext.MessageBox.alert('错误', '请选中需要删除的国别');
        }
    }     
      
    function doDel2(btn)
	{
       if(btn == 'yes')
       {	
			var m = grid.getSelections();
			var jsonData = "[";
	        for(var i = 0, len = m.length; i < len; i++){        		
				var ss = "{\"id\":\"" + m[i].get("id") + "\"}";

				if(i==0)
	           		jsonData = jsonData + ss ;
			   	else
					jsonData = jsonData + "," + ss;	
				ds.remove(m[i]);								
	        }	
			jsonData = jsonData + "]";
			ds.load({params:{start:0, limit:myPageSize, del:true, delData:jsonData}});		
		}
	}
	
   /************************************************************
    *  Add an "clickoutside" event to a Grid.
    *     @param grid {Ext.grid.Grid} The grid to add a clickoutside event to.
    *     The handler is passed the Event and the Grid.
    ************************************************************/	
    function addClickOutsideEvent(grid) {
    	if (!grid.events.clickoutside) {
	    	grid.addEvents({"clickoutside": true});
	    }
	    if (!Ext.grid.Grid.prototype.handleClickOutside) {
	    	Ext.grid.Grid.override({
	    		handleClickOutside: function(e) {
		            var p = Ext.get(e.getTarget()).findParent(".x-grid-container");
		            if (p != this.container.dom) {
				        this.fireEvent("clickoutside", e, grid);
		            }
	    		}
	    	});
	    }
        Ext.get(document.body).on("click", grid.handleClickOutside, grid);
    }

   /************************************************************
    * Create a new dialog - reuse by create and edit part
    ************************************************************/    
    function createNewDialog(dialogName) {
        var thisDialog = new Ext.LayoutDialog(dialogName, {
                modal:true,
                autoTabs:true,
                proxyDrag:true,
                resizable:false,
                width: 480,
                height: 302,
                shadow:true,
				center: {
                    autoScroll: true,
                    tabPosition: 'top',
                    closeOnTab: true,
                    alwaysShowTabs: false
                }
        });
        thisDialog.addKeyListener(27, thisDialog.hide, thisDialog); 
        thisDialog.addButton('取消', function() {thisDialog.hide();});
        
        return thisDialog;
    };
    
   /************************************************************
    * Action - update
    *   handle double click 
    *   user select one of the item and want to update it
    ************************************************************/
    grid.on('rowdblclick', function(grid, rowIndex, e) {
	    var selectedId = ds.data.items[rowIndex].id;

	    // get information from DB and set form now...
	    
		var country_data = new Ext.data.Store({
		    proxy: new Ext.data.ScriptTagProxy({url: window.location.pathname + "../country_loadData?id=" + selectedId}),
			reader: new Ext.data.JsonReader({},['id','countryName','countryCode','validFlag']),
			remoteSort: false
		});
		
		country_data.on('load', function() {
		    
		    // set value now
		    var updateId = country_data.reader.jsonData.id;
		    
		    countryName_show.setValue(country_data.reader.jsonData.countryName);
		    countryCode_show.setValue(country_data.reader.jsonData.countryCode);
		    validFlag_show.setValue(country_data.reader.jsonData.validFlag);
		    
			var updateInstanceDlg;        
	        if (!updateInstanceDlg) {
	            updateInstanceDlg = createNewDialog("a-updateInstance-dlg"); 
	            updateInstanceDlg.addButton('更新', function() {       

			    	// submit now... all the form information are submit to the server
			    	// handle response after that...
			    	if (form_instance_update.isValid()) {
					    form_instance_update.submit({
							
					        params:{id : updateId},
							waitMsg:'正在更新国别信息...',
							reset: false,
							failure: function(form_instance_update, action) {
							    Ext.MessageBox.alert('错误', decodeURI(action.result.errorInfo));
							},
							success: function(form_instance_update, action) {
							   	Ext.MessageBox.alert('确认', decodeURI(action.result.info) );
							    updateInstanceDlg.hide();
							    ds.load({params:{start:0, limit:myPageSize}});
							}
						});					
					}else{
						Ext.MessageBox.alert('错误', '请修正标记出的错误.');
					}	    	
			    });
	           	     
	            var layout = updateInstanceDlg.getLayout();
	            layout.beginUpdate();
	            layout.add('center', new Ext.ContentPanel('a-updateInstance-inner', {title: '更新国别信息'}));
	            layout.endUpdate(); 
	            
	            updateInstanceDlg.show();
	        }  
		}); 
		
		country_data.load();	    
	});
	
	
	 
   /************************************************************
    * Action - add
    *   start to handle add function
    *   new page appear and allow to submit
    ************************************************************/
    
   /************************************************************
    *  To create add new account dialog now....
    ************************************************************/   
    function doAdd(){
        var aAddInstanceDlg;
        
        if (!aAddInstanceDlg) {
            aAddInstanceDlg = createNewDialog("a-addInstance-dlg");       
            aAddInstanceDlg.addButton('重置', resetForm, aAddInstanceDlg);
            aAddInstanceDlg.addButton('保存', function() {       

		    	// submit now... all the form information are submit to the server
		    	// handle response after that...
		    	if (form_instance_create.isValid()) {
				    form_instance_create.submit({
						waitMsg:'正在保存国别信息...',
						reset: false,
						failure: function(form_instance_create, action) {
						    Ext.MessageBox.alert('错误', decodeURI(action.result.errorInfo));
						},
						success: function(form_instance_create, action) {
						    Ext.MessageBox.alert('确认', decodeURI(action.result.info));
						    aAddInstanceDlg.hide();
							
							form_instance_create.reset();
							
						    ds.load({params:{start:0, limit:myPageSize}});
						}
					});					
				}else{
					Ext.MessageBox.alert('错误', '请处理标记的错误.');
				}	    	
		    });

			var layout = aAddInstanceDlg.getLayout();
            layout.beginUpdate();
            layout.add('center', new Ext.ContentPanel('a-addInstance-inner', {title: '创建国别信息'}));
            layout.endUpdate(); 
            
            aAddInstanceDlg.show();
	    };        
	}
	
   /************************************************************
    * Form information - pop-up dialog
	* turn on validation errors beside the field globally
	************************************************************/
   	Ext.form.Field.prototype.msgTarget = 'side';
   	Ext.form.Field.prototype.height = 20;
	Ext.form.Field.prototype.fieldClass = 'text-field-default';
	Ext.form.Field.prototype.focusClass = 'text-field-focus';
	Ext.form.Field.prototype.invalidClass = 'text-field-invalid';

    /************************************************************
    * Create new form to hold user enter information
    * This form is used to create new instance
    ************************************************************/  
    var countryName_tf = new Ext.form.TextField({
        fieldLabel: '国别名称',
        name: 'countryName',
		allowBlank: false
    }); 
    var countryCode_tf = new Ext.form.TextField({
        fieldLabel: '国别代码',
        name: 'countryCode',
		allowBlank: false
    }); 
    
    var form_instance_create = new Ext.form.Form({
        labelAlign: 'right',
		url: window.location.pathname + "../country_createData"
    });

    form_instance_create.column({width: 430, labelWidth:120, style:'margin-left:8px;margin-top:8px'});
    form_instance_create.fieldset(
        {id:'desc', legend:'填写新的国别信息'},
        countryName_tf,
        countryCode_tf
	);
	
    form_instance_create.applyIfToFields({width:255});
    form_instance_create.render('a-addInstance-form');
    form_instance_create.end();
    
    resetForm = function() {
		countryName_tf.setValue('');
		countryCode_tf.setValue('');
	};
     

    /************************************************************
    * Create form to hold user enter information
    * This form is used to update current instance
    ************************************************************/   
    var countryName_show = new Ext.form.TextField({
        fieldLabel: '国别名称',
        name: 'countryName',
		allowBlank: false
    }); 
	
    var countryCode_show = new Ext.form.TextField({
        fieldLabel: '国别代码',
        name: 'countryCode',
		allowBlank: false
    }); 
	
    var validFlag_show = new Ext.form.ComboBox({
        store: new Ext.data.SimpleStore({
            fields: ['stateCode','stateName'],
            data : validFlagArray
        }),		
		//name: 'validFlag',
		hiddenName:'validFlag',
		fieldLabel: '有效标记',
        displayField:'stateName',
		valueField : 'stateCode',
        typeAhead: true,
		editable: false,
        mode: 'local',
        triggerAction: 'all',
        emptyText:'选择一种状态...',
        selectOnFocus:true,
        resizable:false
    });	

	
	
    var form_instance_update = new Ext.form.Form({
        labelAlign: 'right',
		url: window.location.pathname + "../country_updateData"
    });

    form_instance_update.column({width: 430, labelWidth:120, style:'margin-left:8px;margin-top:8px'});
    form_instance_update.fieldset(
        {id:'', legend:'更新国别信息'},
        countryName_show,
        countryCode_show,
        validFlag_show
	);
	
    form_instance_update.applyIfToFields({width:255});
    form_instance_update.render('a-updateInstance-form');
    form_instance_update.end();
    
});