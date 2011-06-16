


/** 
 * @auther huangfeng 
 * @class   Ext.ux.GridExtend 
 * 通用的grid 
 */ 
Ext.ux.GridExtend=function(config){ 
	
    Ext.QuickTips.init(); 
    this.config=config; 


    this.filters=this.config.filters||  '' ; 
    
    this.id = this.config.id ;
    
    this.filterItem = this.config.filterItem;
    
    this.filterComboxFields = this.config.filterComboxFields;
    
    
     
    /** 
     *  @param  {String} 
     * 显示列表的id 
     */ 
    this.el=this.config.el||document.body; 
    /** 
     * @param   {String} 
     * 读取编辑数据的form的url 
     */ 
    this.editUrl=this.config.editUrl; 
    /** 
     * @param   {String} 
     * 读取编辑数据的form的url 
     */ 
    this.deleteUrl=this.config.deleteUrl; 
    /** 
     * @param   {String} 
     * 读取列表数据的url 
     */ 
    this.storeUrl=this.config.storeUrl; 
    /** 
     * @param   {String} 
     * 保存添加到列表数据的url 
     */ 
    this.formSaveUrl=this.config.formSaveUrl; 
    /** 
     * @param   {String} 
     * 列表的标题 
     */ 
    this.title=this.config.title||''; 
    
    
    
     /** 
     * @param   {String} 
     * 导出的url 
     */ 
    this.exportUrl=this.config.exportUrl; 
    
     /** 
     * @param   {String} 
     * 每页显示条数 
     */ 
    this.pageSize = this.config.pageSize||15;
    
    this.defaultSortColumn = this.config.defaultSortColumn || this.id;
    
    this.defaultSort = this.config.defaultSort || 'Desc';
     
    /** 
     * @param   {Object} 
     * 默认情况下有此显现,如果不需要可以为false 
     * 必须含有下列参数:<br> 
     * loadingEL        {String}    加载页面时显示的内容id<br> 
     * loadingMaskEl    {String}    渐显的内容id<br>  
     */  
    this.loadingMark=this.config.loadingMark||{ 
        loadingEL:'正在加载。。。', 
        loadingMaskEL:'loading-mask' 
    }; 
    /** 
     * @param   {Array} 
     * 列表顶部状态栏 
     */ 
    this.tbar=this.config.tbar||[                   //grid顶部栏位
    	
    	'关键字:',new Ext.form.TextField({
	             fieldLabel: '关键字',
	             name:'filterValue',
	             id : 'filterValue'
        }),'-', new Ext.form.ComboBox({
		        store: new Ext.data.SimpleStore({
		            fields: ['fieldName','fieldText'],
		            data : this.filterComboxFields
		        }),		
				hiddenName:'fieldName',
				fieldLabel: '过滤器',
		        displayField:'fieldText',
				valueField : 'fieldName',
		        typeAhead: true,
				editable: false,
		        mode: 'local',
		        triggerAction: 'all',
		        emptyText:'选择过滤器',
		        selectOnFocus:true,
		        resizable:false
   		 }),'-',{ 
            text:'查询',                  //按钮的名称 
            tooltip:'查询',             //鼠标停留在按钮上的提示 
            iconCls:'search',                  //按钮图表的类 
           // icon:'js/ext/resources/myimages/add.png',
            handler:this.search.createDelegate(this)           //处理按钮被点击时触发的时间函数 
        }, '-',{ 
            text:'添加',                  //按钮的名称 
            tooltip:'添加数据',             //鼠标停留在按钮上的提示 
            iconCls:'add',                  //按钮图表的类 
            handler:this.newInfo.createDelegate(this)           //处理按钮被点击时触发的时间函数 
        },'-',{//'-'为多按钮间隔符 
            text:'删除',                  //删除按钮的名称 
            tooltip:'删除数据',             //鼠标停留在按钮上的提示 
            iconCls:'remove',               //按钮图表的类 
            handler:this.handlerDelete.createDelegate(this)         //处理按钮被点击时触发的时间函数 
        },'-',{ 
            text:'导出',                 //删除按钮的名称 
            tooltip:'导出',              //鼠标停留在按钮上的提示 
            iconCls:'export', 
            handler:this.exportExcel.createDelegate(this)           //处理按钮被点击时触发的时间函数 
        }]; 


    /** 
     * @param   选择框对象 
     */ 
    this.sm=this.config.sm||new Ext.grid.CheckboxSelectionModel({//start Ext.grid.CheckboxSelectionModel 
        singleSelect:false                  //是否只能选择一个 
    }); 
    /** 
     * @param   {Array} 
     * 列表的栏的前面数据 
     */ 
    this.cmDataArrayBefore=[//start Ext.grid.ColumnModel 
        //defaultSortable:true,             //默认情况下为排序 
        new Ext.grid.RowNumberer(),         //数据函数序列号 
        this.sm 
    ]; 
    /** 
     * @param   {Array} 
     * 显示的内容是从后台读取出来的数据,所以此数据中的dataIndex属性要与<br> 
     * 从后台读取的字段要一致 
     */ 
    this.cmDataArray=this.config.cmDataArray||[]; 
    /** 
     * @param   {Array} 
     * 列表的栏的后面数据 
     */ 
    this.cmDataArrayAfter=this.config.cmDataArrayAfter||[]; 
    /** 
     * @param   {Ext.grid.ColumnModel} 
     * 列表的栏位数据 
     */ 
    this.cm=this.config.cm||new Ext.grid.ColumnModel( 
            this.cmDataArrayBefore.concat(this.cmDataArray).concat(this.cmDataArrayAfter) 
    );//end Ext.grid.ColumnModel     
     
    this.cm.defaultSortable = true;
    
    /** 
     * @param   {Array} 
     * 读取gridStore数据的字段设置数据对象 
     */ 
    this.gridStoreFields=this.config.gridStoreFields||new Array(); 
     
    /* 
     * 如果this.gridStoreFields中没有数据,把this.cmDataArray中的dataIndex的属性值<br> 
     * 赋予gridStoreFields数组中对象的name属性值 
     */ 
    if(this.gridStoreFields.length==0){ 
        for(var i=0,len=this.cmDataArray.length;i<len;i++){ 
            this.gridStoreFields[i]={name:this.cmDataArray[i].dataIndex}; 
        } 
    } 
     
    /** 
     * @param   {new Ext.data.Store} 
     * 从后台读取的列表内容 
     */  
    this.gridStore=this.config.gridStore||new Ext.data.Store({//start Ext.data.Store 
        proxy:new Ext.data.HttpProxy({ 
            url:this.storeUrl                   //读取数据的url
        }), 
        reader:new Ext.data.JsonReader({//start Ext.data.JsonReader 
            root:'results',                    //存储数据的属性 
            totalProperty:'totalCount',        //总共的页数 
            id: this.id                //每行的id值 
        },                                  //end Ext.data.JsonReader 
        this.gridStoreFields),
		sortInfo: {field: this.defaultSortColumn, direction:this.defaultSort}
        
    });//end Ext.data.Store 
     
    this.gridStore.load({params:{start: 0, limit: this.pageSize}}); 
    /** 
     * @param   {Ext.PagingToolbar} 
     * 底部状态栏 
     */ 
    this.bbar=this.config.bbar||new Ext.PagingToolbar({     //在grid底层添加分页工具栏 
            pageSize:this.pageSize,                    //显示的数据条数 
            store:this.gridStore,           //选择翻页按钮时的加载到grid的数据 
            displayInfo:true,               //设置是否显示分页栏的信息 
            displayMsg:'显示第 {0} 条到 {1} 条记录，共 {2} 条',//消息栏的页数信息 
            emptyMsg:"没有记录"             //当没有记录时要显示的信息 
        });//end bbar 
    /** 
     * 如果要设置此参数必须在cm中设置相应的id 
     */ 
    this.autoExpandColumn=this.config.autoExpandColumn||''; 
         
    
    /** 
     * @param   {Ext.grid.GridPanel} 
     * @private 
     */ 
    this.grid=this.config.gridStore||new Ext.grid.GridPanel({ 
        //el:this.el,                           //显示grid对象的层ID. 
        store:this.gridStore,                   // grid装载的数据. 
        viewConfig:{ 
            autoFill:true, 
            deferEmptyText:'请等待...', 
            emptyText:'没有数据', 
            enableRowBody:true 
        },  
        
        
        sm:this.sm,                             //在每行数据前添加的一组checkBox 
        height:Ext.get(this.el).getComputedHeight(), 
        autoHeight:true, 
        loadMask:true,  
        maskDisabled:true, 
        cm:this.cm,                             //设置栏位. 
        title:this.title,                       //标题名称. 
        iconCls:'icon-grid',                //标题前的图片css类 
        autoExpandColumn:this.autoExpandColumn, 
        plugins: new Ext.grid.GridFilters({filters:this.filters}) , 
        bbar:this.bbar, 
        tbar:this.tbar 
    }); 
   
    /*  新增数据的列
     * 
     * 
     * */
    
    this.formFields=this.config.formFields||new Array(); 
     

    
    
    /** 
     * 双击数据的事件，弹出一个编辑此条数据的层  
     * @param   grid        此列表的对象 
     * @param   rowIndex    在列表中的行数 
     * @param   e           触发此事件的事件对象 
     */ 
    this.rowdblclick=this.config.rowdblclick||function(grid, rowIndex, e){ 
        var selectId=this.gridStore.data.items[rowIndex].id; 
        this.editInfo(selectId); 
    }; 
    this.grid.on('rowdblclick',this.rowdblclick.createDelegate(this)); 
    this.grid.render(this.el); 
     
    
    //当js加载完毕后删除loading页面并渐显内容 
    if(this.loadingMark){ 
        setTimeout(function(){ 
             Ext.get(this.loadingMark.loadingEL).remove(); 
             Ext.get(this.loadingMark.loadingMaskEL).fadeOut({remove:true}); 
        }.createDelegate(this), 250); 
    }    
} 


Ext.ux.GridExtend.prototype={ 
	
	  search:function(){ 
    	var fieldName=Ext.get('fieldName').dom.value ;
        var filterValue=Ext.get('filterValue').dom.value ;
       // alert(fieldName);
        this.gridStore.load({params : {start : 0,limit : this.pageSize,fieldName:fieldName,filterValue:filterValue}}); 

      }, 
	
	
    /** 
     * 加载 form表单的数据 
     * @param   selectId    选择此条数据的唯一标识码，此参数发送到后台处理 
     */ 
    editInfo:function(selectId){ 
    	
    	//this.formFields.push([{xtype: 'hidden', id: 'id',name: 'id',hidden: 'true' ,value : selectId}]);
        var form=this.getForm(); 
        /* 
        form.form.load({//start load 参数设置 
            url:this.editUrl, 
            params:{ 
                id:selectId 
            }, 
            waitMsg:'正在加载........' 
        });//end load 参数设置 */
         var record=this.grid.getSelectionModel().getSelected();
        // alert(record.get("id"));
        this.formWindow(form,'编辑',selectId); 
        form.form.loadRecord(record); 
    }, 
    getForm:function(){ 
        //错误信息放在右边 
        Ext.form.Field.prototype.msgTarget = 'side'; 
        var formSaveUrl=this.formSaveUrl; 
        var formFields=this.formFields; 
        //var formSaveUrl=Ext.clone(this.formSaveUrl); 
       // var formFields=Ext.clone(this.formFields); 
         
        //实例化form面板 
        var form=new Ext.form.FormPanel({//start Ext.form.FormPanel 
            baseCls:'x-plain', 
            url:this.formSaveUrl, 
            frame:true, 
            id:'form', 
            items:formFields 
        });//end Ext.form.FormPanel 
         
        return form; 
    }, 
    /** 
     * 装form表单的窗口 
     * @param   form            要装载的form 
     * @param   titlePre        标题的前缀 
     */ 
    formWindow:function(form,titlePre,id){ 
        //实例化窗口 
        this.window=new Ext.Window({//start Ext.Window 
            title:titlePre+'任务列表', 
            width:500, 
            height:500, 
            minWidth:300, 
            minHeight:300, 
            layout:'fit', 
            //closeAction:'hide', 
            plain:true, 
            bodyStyle:'padding:5px', 
            iconCls:'form', 
            buttonAlign:'center', 
            items:form, 
            modal:true, 
             
            buttons:[{                          //按钮 
                text:'保存', 
                handler:(function(){//start function按钮处理函数 
                    if(form.getForm().isValid()){//表单是否通过交验，通过责提交表单，否则弹出错误窗口 
                        form.getForm().submit({ 
                            scope:this, 
                            waitMsg:'正在保存数据...',
                            params: {  
                         		id:id 
                   			 },
                            success:function(form,action){ 
                                Ext.MessageBox.alert('消息：','保存成功'); 
                                this.grid.store.reload(); 
                                this.window.close() ; 
                            }, 
                            failure:function(form,action){ 
                                Ext.MessageBox.alert('有错误:', action.result.errors); 
                                this.window.close() ; 
                            } 
                        }); 
                    }else{ 
                        Ext.MessageBox.alert('有错误：','表单填写有错误！'); 
                    } 
                     
                }).createDelegate(this)//end function                
            },{ 
                text:'重置', 
                handler:function(){//start function按钮处理函数 
                    form.getForm().reset(); 
                }//end function  
            }] 
        });//end Ext.Window 


        //显示窗口 
        this.window.show(); 
    }, 
    /** 
     * 添加列表新数据的函数 
     */ 
    newInfo:function(){//start newTableInfo 
        this.formWindow(this.getForm(),'添加',null);        
    },//end newTableInfo 
    /** 
     * 删除数据，并把此数据的唯一标识码发送到后台 
     */ 
    handlerDelete:function(){//start deleteRecord 
        this.sendId(this.deleteUrl);
    },//end deleteRecord 
    sendId:function(url){//start deleteRecord 
        var ids=new Array();                //存放uniqueCode的数组 
        var delids ="" ;
        var records=this.grid.selModel.selections.items;//grid中被选择的数据，类型为Array 
        for(var i = 0, len = records.length; i < len; i++){ 
            ids[ids.length]=records[i].id;  //把数据id赋予ids数组中 
            delids = delids+records[i].id+",";
        } 
        
        Ext.Ajax.request({                  //调用Ajax发送请求到后台 
            scope:this, 
            url:url,                    //删除grid数据的url. 
            params: {  
                         delids:delids 
                    },
            success:function(transport){                //处理成功后返回的函数 
                var oXmlDom=transport.responseText;
                
                if(oXmlDom!=''){ 
                    var content=eval(oXmlDom); 
                    Ext.MessageBox.alert("有错误：",content.errors) 
                }else{ 
                	Ext.MessageBox.alert('消息','删除成功!'); 
                    this.grid.store.reload();       //重新加载grid中的数据 
                } 
            }, 
            failure:function(){             //处理后台删除失败的函数 
                Ext.MessageBox.alert('消息','删除失败!'); 
            } 
        }); 
         
    },//end deleteRecord 
    exportExcel:function(){ 
    	
    	var fieldName=Ext.get('fieldName').dom.value ;
        var filterValue=Ext.get('filterValue').dom.value ;
       
         Ext.Ajax.request({                  //调用Ajax发送请求到后台 
            scope:this, 
            url:this.exportUrl,                    //删除grid数据的url. 
            params: {  
                         fieldName:fieldName, 
                         filterValue:filterValue
                    },
            success:function(transport){                //处理成功后返回的函数 
                var oXmlDom=transport.responseText;
                
                if(oXmlDom!=''){ 
                    var content=eval(oXmlDom); 
                    Ext.MessageBox.alert("有错误：",content.errors) 
                }else{ 
                	Ext.MessageBox.alert('消息','删除成功!'); 
                    this.grid.store.reload();       //重新加载grid中的数据 
                } 
            }, 
            failure:function(){             //处理后台删除失败的函数 
                Ext.MessageBox.alert('消息','删除失败!'); 
            } 
        }); 
        
    } 
} 


	//填充图片的本地应用 
	//Ext.BLANK_IMAGE_URL='js/ext/resources/myimages/add.png'; 
	
	//在ie中默认的宽度不够 
	Ext.apply(Ext.MessageBox,{ 
	    alert:function(title, msg, fn, scope){ 
	        this.show({ 
	            title : title, 
	            msg : msg, 
	            buttons: this.OK, 
	            minWidth:200, 
	            fn: fn, 
	            scope : scope 
	        }); 
	        return this; 
	    } 
	});

 
	
	Ext.menu.RangeMenu.prototype.icons= {  gt: 'js/ext/resources/extendIamges/greater_then.png',lt: 'ext/resources/extendIamges/less_then.png',  eq: 'ext/resources/extendIamges/equals.png' }; 
	
	
	
	Ext.grid.filter.StringFilter.prototype.icon = 'ext/resources/extendIamges/find.png';

/*  
 * 修改filter发送到后台的数据模式. 
 * 例:filter.[i][field]、filter.[i][type]、filter.[i][value]、filter.[i][comparison]、 
 *  
 */ 


	Ext.grid.GridFilters.prototype.buildQuery=function(filters){ 
	    var p = {}; 
        for(var i=0, len=filters.length; i<len; i++) { 
            var f = filters[i]; 
            var root = [this.paramPrefix, '[', i, ']'].join(''); 
            p[root + '[field]'] = f.field; 
            //修改此处 
            var dataPrefix = root; 
            for(var key in f.data) { 
                p[[dataPrefix, '[', key, ']'].join('')] = f.data[key]; 
      		} 
        }  
        
	    return p; 
	    
	} 


	//添加时间交验函数 
	Ext.apply(Ext.form.VTypes, {     
	    //时间交验属性 
	    daterange: function(val, field) { 
	        var date = field.parseDate(val); 
	         
	        var dispUpd = function(picker) { 
	          var ad = picker.activeDate; 
	          picker.activeDate = null; 
	          picker.update(ad); 
	        }; 
	
	
	        //debugger; 
	        if (field.startDateField) { 
	          var sd = field.startDateField; 
	          sd.maxValue = date; 
	          if (sd.menu && sd.menu.picker) { 
	            sd.menu.picker.maxDate = date; 
	            dispUpd(sd.menu.picker); 
	          } 
	        } else if (field.endDateField) { 
	          var ed = field.endDateField; 
	          ed.minValue = date; 
	          if (ed.menu && ed.menu.picker) { 
	            ed.menu.picker.minDate = date; 
	            dispUpd(ed.menu.picker); 
	          } 
	        } 
	        return true; 
	  } 
}); 
Ext.QuickTips.init(); 
