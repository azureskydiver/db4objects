using System;
using System.Collections.Generic;
using System.Collections;
using System.Reflection;
using System.Drawing;
using System.Windows.Forms;

using OManager.BusinessLayer.Common;
using OManager.BusinessLayer.ObjectExplorer;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary.Common
{
	public partial class dbDataGridView : DataGridView
	{
		#region Private Members

		private ContextMenuStrip m_columnContextMenuStrip; //column context menu
		private ContextMenuStrip m_rowContextMenuStrip; //Row context menu
		private string SHOW_ALL_COLUMN = "SHOW_ALL_COLUMN";
		private bool m_CellClick = false;
		private int m_CurrentRowNumber = 0;


		public event System.EventHandler<dbDataGridViewEventArgs> OnDBGridCellClick;

		//Constants
		private const string MENU_SEPARATOR = "MENU_SEPARATOR";
		private const string COLUMN_NO_NAME = "No.";
		private const string VALUE_NULL = "null";
		#endregion

		#region Constructor

		public dbDataGridView()
		{
			this.SetStyle(ControlStyles.CacheText |
				 ControlStyles.AllPaintingInWmPaint |
				 ControlStyles.UserPaint |
				 ControlStyles.OptimizedDoubleBuffer |
				 ControlStyles.Opaque, true);
			SetDefaultProperties();

		}

		#endregion

		#region Override Methods

		protected override void OnDragDrop(DragEventArgs e)
		{
			try
			{
				base.OnDragDrop(e);

				Point ptToClient = this.PointToClient(new Point(e.X, e.Y));

				DragHelper.ImageList_DragMove(ptToClient.X, ptToClient.Y);
				e.Effect = DragDropEffects.Move;

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		protected override void OnDragOver(DragEventArgs e)
		{
			base.OnDragOver(e);

			e.Effect = DragDropEffects.Move;
		}

		protected override void OnDragEnter(DragEventArgs e)
		{
			try
			{

				base.OnDragEnter(e);
				e.Effect = DragDropEffects.Move;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		protected override void OnMouseDown(MouseEventArgs e)
		{
			try
			{
				//Reset m_CellClick, as when user left clicks on the same cell,
				//(mostly in case of multiple row selection) we need to fire the
				//OnDBGridCellClick event
				m_CellClick = false;
				base.OnMouseDown(e);

				this.DoubleBuffered = false;

				//set to false for users to receive visual feedback when when reordering columns.
				DataGridView.HitTestInfo hitTestInfo = this.HitTest(e.X, e.Y);

				//setting member variable IsHeaderClicked = true, to set cancel 
				// contextmenu on ColumnHeader.
				DataGridViewHitTestType hitTestType = hitTestInfo.Type;
				if (e.Button == MouseButtons.Right &&
					hitTestType == DataGridViewHitTestType.ColumnHeader ||
					hitTestType == DataGridViewHitTestType.RowHeader ||
					hitTestType == DataGridViewHitTestType.TopLeftHeader)
				{
					this.ContextMenuStrip = m_columnContextMenuStrip;
				}
				else if (hitTestType == DataGridViewHitTestType.Cell)
				{
					this.ContextMenuStrip = m_rowContextMenuStrip;
				}

				if (this.Rows.Count < 1)
				{
					if (this.ContextMenuStrip != null)
						this.ContextMenuStrip = null;

					return;
				}

				//If user clicks on blank space below grid, then remove the context menu
				if (hitTestInfo.Type == DataGridViewHitTestType.None)
				{
					this.ContextMenuStrip = null;
					return;
				}

				if (hitTestInfo.Type != DataGridViewHitTestType.Cell && this.CurrentRow != null)
				{
					this.m_CurrentRowNumber = this.CurrentRow.Index;
					return;
				}

				this.m_CurrentRowNumber = hitTestInfo.RowIndex;
				if (hitTestInfo.RowIndex == Common.Constants.INVALID_INDEX_VALUE)
					return;

				if (e.Button == MouseButtons.Right)
				{
					if (this.SelectedRows.Count < 2)
						this.ClearSelection();
					if (this.SelectedRows.Count < 1)
						this.Rows[hitTestInfo.RowIndex].Selected = true;
				}

				if (this.CurrentRow != null)
					this.CurrentRow.Visible = true;

				//fireing OnDBCellClick event.
				if (!m_CellClick && e.Button == MouseButtons.Left)
				{
					if (OnDBGridCellClick != null)
					{
						dbDataGridViewEventArgs objCurrentRowData;
						objCurrentRowData = new dbDataGridViewEventArgs(this.CurrentCell);
						OnDBGridCellClick(this, objCurrentRowData);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
				ContextMenuStrip = null;
			}
		}

		protected override void OnCurrentCellChanged(EventArgs e)
		{
			base.OnCurrentCellChanged(e);

			//fireing OnDBCellClick event.
			try
			{
				if (this.CurrentCell == null)
					return;

				//Does not fire OnAICellClick event if current row is not 
				//chage for the cell or cell is header cell.
				if (this.CurrentCell.RowIndex == Constants.INVALID_INDEX_VALUE ||
					this.CurrentCell.RowIndex == this.m_CurrentRowNumber)
				{
					return;
				}

				this.m_CurrentRowNumber = this.CurrentCell.RowIndex;

				this.CurrentRow.Visible = true;

				if (!m_CellClick)
				{
					if (OnDBGridCellClick != null)
					{
						dbDataGridViewEventArgs objCurrentRowData;
						objCurrentRowData = new dbDataGridViewEventArgs(this.CurrentCell);
						OnDBGridCellClick(this, objCurrentRowData);
					}
					m_CellClick = true;
				}
				else
					m_CellClick = false;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region Public Method

		/// <summary>
		/// Build context menu for column to set column visibility.
		/// </summary>
		/// <param name="columnName">Name of column, allways visible</param>
		/// <param name="showAllMenuText">Text for show all column.</param>
		public void BuildColumnContextMenu(string[] columnName, string showAllMenuText)
		{
			DataGridViewColumn column;
			int colCount = this.ColumnCount;
			int visibaleColumnCount = 0;

			m_columnContextMenuStrip = new ContextMenuStrip();

			string menuName = string.Empty;
			string menuText = string.Empty;

			string showAllColumnText = showAllMenuText;
			ToolStripMenuItem objMenu;

			try
			{
				OMETrace.WriteFunctionStart();

				for (int colIndex = 0; colIndex < colCount; ++colIndex)
				{
					column = this.Columns[colIndex];
					menuText = column.HeaderText;
					menuName = column.Name;

					if (column.Visible && string.IsNullOrEmpty(menuText) == false)
					{
						++visibaleColumnCount;
						objMenu = new ToolStripMenuItem(menuText);
						objMenu.Name = menuName;
						objMenu.Checked = true;
						m_columnContextMenuStrip.Items.Add(objMenu);
					}
				}

				//if only visible column count is one then do not need any context menu.
				//to show and hide it.
				if (visibaleColumnCount < 2)
				{
					m_columnContextMenuStrip = null;
					return;
				}

				//disable menu item for column 
				for (int count = 0; count < columnName.Length; count++)
				{
					if (m_columnContextMenuStrip.Items.ContainsKey(columnName[count]))
						m_columnContextMenuStrip.Items[columnName[count]].Enabled = false;
				}

				if (m_columnContextMenuStrip.Items.Count > 0)
				{
					//add separator before showall menu item
					ToolStripSeparator toolStripSeparator = new System.Windows.Forms.ToolStripSeparator();
					toolStripSeparator.Name = MENU_SEPARATOR;
					m_columnContextMenuStrip.Items.Add(toolStripSeparator);

					//add show all menu item
					menuText = showAllColumnText;
					menuName = SHOW_ALL_COLUMN;
					objMenu = new ToolStripMenuItem(menuText);
					objMenu.Name = menuName;
					m_columnContextMenuStrip.Items.Add(objMenu);
				}

				this.ContextMenuStrip = m_columnContextMenuStrip;
				this.ContextMenuStrip.ItemClicked +=
					new ToolStripItemClickedEventHandler(ContextMenuStrip_ItemClicked);

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		/// <summary>
		/// Sync. the column visibility while toggeling between perespective view and
		/// normal view.
		/// </summary>
		public void SetColumnVisible()
		{
			try
			{
				if (m_columnContextMenuStrip == null)
					return;

				DataGridViewColumn column;
				int menuCount = m_columnContextMenuStrip.Items.Count;
				ToolStripMenuItem objMenuItem;
				for (int iMenuIndex = 0; iMenuIndex < menuCount; ++iMenuIndex)
				{
					objMenuItem =
						this.m_columnContextMenuStrip.Items[iMenuIndex] as ToolStripMenuItem;

					if (objMenuItem != null && objMenuItem.Enabled &&
						objMenuItem.Name != SHOW_ALL_COLUMN)
					{
						column = this.Columns[objMenuItem.Name];
						if (objMenuItem.Checked)
						{
							column.Visible = true;
							objMenuItem.Checked = true;
						}
						else
						{
							column.Visible = false;
							objMenuItem.Checked = false;
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}


		public void BuildRowContextMenu()
		{

			m_rowContextMenuStrip = new ContextMenuStrip();

			string menuName = string.Empty;
			string menuText = string.Empty;

			ToolStripMenuItem objMenu;

			try
			{
				menuText = Helper.GetResourceString(Constants.CONTEXT_MENU_CAPTION.Replace(Constants.HOT_KEY_CHAR, string.Empty));
				menuName = Constants.CONTEXT_MENU_DATAGRID_DELETE;

				objMenu = new ToolStripMenuItem(menuText);
				objMenu.Name = menuName;

				m_rowContextMenuStrip.Items.Add(objMenu);

				this.ContextMenuStrip = m_rowContextMenuStrip;
				this.ContextMenuStrip.ItemClicked +=
					new ToolStripItemClickedEventHandler(ContextMenuStrip_ItemClicked);
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}
		#endregion

		#region Private Events

		private void ContextMenuStrip_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
		{
			DataGridViewColumn column;
			string menuItemName = e.ClickedItem.Name;

			try
			{
				if (this.Columns.Contains(menuItemName))
				{
					column = this.Columns[menuItemName];

					//do not process if column index is zero.
					//making sure atleast one column should be visible.
					ToolStripMenuItem menuItem =
						this.ContextMenuStrip.Items[menuItemName] as ToolStripMenuItem;

					if (column.Visible)
					{
						column.Visible = false;
						menuItem.Checked = false;
					}
					else
					{
						column.Visible = true;
						menuItem.Checked = true;
					}
				}
				else if (menuItemName == SHOW_ALL_COLUMN)
				{
					//show all columns when show all column menu is clicked.
					ShowAllColumn();
				}

				if (e.ClickedItem.Name == Constants.CONTEXT_MENU_DATAGRID_DELETE)
				{
					ToolStripMenuItem menuItem = (ToolStripMenuItem)ContextMenuStrip.Items[0];
					menuItem.Checked = false;

					this.ContextMenuStrip.Dispose();
					//m_rowContextMenuStrip.Dispose();

					if (this.SelectedRows.Count > 1)
					{
						List<DataGridViewRow> selectedRows = new List<DataGridViewRow>();

						int rowCount = this.Rows.Count;
						for (int i = 0; i < rowCount; i++)
						{
							if (this.Rows[i].Selected)
								selectedRows.Add(this.Rows[i]);
						}

						for (int i = 0; i < selectedRows.Count; i++)
						{
							this.Rows.Remove(selectedRows[i]);
						}
						selectedRows.Clear();
					}
					else
						this.Rows.RemoveAt(m_CurrentRowNumber);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
			finally
			{
				this.ContextMenu = null;
				this.ContextMenuStrip = null;
			}
		}

		#endregion

		#region Private Methods

		/// <summary>
		/// Shows all the columns 
		/// fires clicked on the "Show all columns" from column context menu.
		/// </summary>
		private void ShowAllColumn()
		{
			try
			{
				if (m_columnContextMenuStrip == null)
					return;
				string containerForm = string.Empty;
				DataGridViewColumn column;
				int menuCount = m_columnContextMenuStrip.Items.Count;
				ToolStripMenuItem objMenuItem;
				for (int iMenuIndex = 0; iMenuIndex < menuCount; iMenuIndex++)
				{
					objMenuItem =
						this.m_columnContextMenuStrip.Items[iMenuIndex] as ToolStripMenuItem;

					if (objMenuItem != null && objMenuItem.Enabled &&
						objMenuItem.Name != SHOW_ALL_COLUMN)
					{
						column = this.Columns[objMenuItem.Name];
						column.Visible = true;
						objMenuItem.Checked = true;
					}
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		/// <summary>
		/// This method is used to set default behaviour of the DataGridView.
		/// </summary>
		private void SetDefaultProperties()
		{
			this.BackgroundColor = Color.White;
			this.RowHeadersVisible = false;
			this.AutoGenerateColumns = false;
			this.AllowUserToResizeRows = false;
			this.AllowUserToAddRows = false;
			this.AllowUserToOrderColumns = false;
			this.AllowDrop = true;
			this.MultiSelect = false;
			this.EditMode = DataGridViewEditMode.EditOnEnter;
			this.ScrollBars = ScrollBars.Both;
			this.ColumnHeadersHeightSizeMode = DataGridViewColumnHeadersHeightSizeMode.AutoSize;
			this.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
			this.AllowUserToDeleteRows = false;
			this.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.None;
			this.AutoSizeRowsMode = DataGridViewAutoSizeRowsMode.None;
			this.Visible = true;
			this.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.None;
			this.AutoGenerateColumns = false;
			this.EnableHeadersVisualStyles = false;
			this.DefaultCellStyle.Alignment = DataGridViewContentAlignment.TopLeft;
			DataGridViewCellStyle cellstyle = new DataGridViewCellStyle();
			cellstyle.BackColor = Color.FromArgb(218, 232, 241);
			SetColumnHeaderStyle();
		}

		public void SetColumnHeaderStyle()
		{
			try
			{
				DataGridViewCellStyle headerCellStyle = new DataGridViewCellStyle();
				float fontSize = 8;
				Font headerFont = new Font("Tahoma", fontSize, FontStyle.Regular);
				headerCellStyle.ForeColor = Color.Black;
				headerCellStyle.BackColor = SystemColors.Control;
				headerCellStyle.Font = headerFont;
				this.ColumnHeadersHeight = 20;
				this.ColumnHeadersDefaultCellStyle = headerCellStyle;

				DataGridViewCellStyle cellstyle = new DataGridViewCellStyle();
				cellstyle.Font = headerFont;
				this.DefaultCellStyle = cellstyle;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		#endregion

		#region Fill QueryResult Datagrid methods

		/// <summary>
		/// 
		/// </summary>
		/// <param name="hashColumn"></param>
		/// <param name="className"></param>
		/// <param name="hashAttributes"></param>
		internal void SetDataGridColumnHeader(Hashtable hashColumn, string className, Hashtable hashAttributes)
		{
			try
			{
				this.Columns.Add(Common.Constants.QUERY_GRID_ISEDITED_HIDDEN, Common.Constants.QUERY_GRID_ISEDITED_HIDDEN);
				this.Columns.Add(COLUMN_NO_NAME, COLUMN_NO_NAME);
				this.Columns[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Visible = false;
				this.Columns[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Width = 0;
				this.Columns[COLUMN_NO_NAME].Width = 100;

				IDictionaryEnumerator eNum = hashColumn.GetEnumerator();
				if (eNum != null)
				{
					while (eNum.MoveNext())
					{
						if (!eNum.Key.ToString().Equals(BusinessConstants.DB4OBJECTS_REF))
						{
							dbDataGridViewDateTimePickerColumn valueTextBoxColumn =
							CreateDateTimeAndComboBoxColumn(eNum.Key.ToString(), eNum.Key.ToString(), DataGridViewColumnSortMode.Automatic);
							this.Columns.Add(valueTextBoxColumn);

							if (hashAttributes.Count != 0)
							{
								string strTag = hashAttributes[eNum.Key.ToString()] as string;
								this.Columns[eNum.Key.ToString()].Tag = strTag;
							}
							else
							{
								this.Columns[eNum.Key.ToString()].Tag = className;
							}
						}
					}

					string[] colNames = new string[] { COLUMN_NO_NAME };
					this.BuildColumnContextMenu(colNames, Helper.GetResourceString(Constants.GRID_SHOW_ALL_COLUMN));
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		
		internal void SetDataGridColumnHeader(List<Hashtable> resultList, string className, Hashtable hashAttributes)
		{
			this.Rows.Clear();
			this.Columns.Clear();
			SetDataGridColumnHeader(BiggestFieldList(resultList), className, hashAttributes);
		}

		private static Hashtable BiggestFieldList(IList<Hashtable> resultList)
		{
			if (resultList.Count == 0)
			{
				return new Hashtable();
			}

			Hashtable hColumn = resultList[0];
			foreach (Hashtable current in resultList)
			{
				if (current.Count < hColumn.Count)
				{
					hColumn = current;
				}
			}
			
			return hColumn;
		}


		internal void SetDatagridRowsWithIndex(List<Hashtable> resultList, string className, Hashtable hashAttributes, int index)
		{
			try
			{
				this.Rows.Clear();

				for (int listCount = 0; listCount < resultList.Count; listCount++)
				{
					Hashtable hTable = resultList[listCount];
					IDictionaryEnumerator eNum = hTable.GetEnumerator();
					DataGridViewRow newRow = new DataGridViewRow();

					this.Rows.Add(newRow);
					this.Rows[listCount].Cells[COLUMN_NO_NAME].Value = index++;
					this.Rows[listCount].Cells[COLUMN_NO_NAME].ReadOnly = true;

					// For checking if row is edited or not
					this.Rows[listCount].Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = false;


					if (eNum != null)
					{
						while (eNum.MoveNext())
						{
							if (!eNum.Key.ToString().Equals(BusinessConstants.DB4OBJECTS_REF))
							{
								string dataType = null;
								if (hashAttributes.Count == 0)
								{
									dataType = Helper.DbInteraction.GetDatatype(className, eNum.Key.ToString());
								}
								else
								{
									int intIndex = eNum.Key.ToString().LastIndexOf('.');
									string strAttribName = eNum.Key.ToString().Substring(intIndex + 1);
									string clsName = this.Columns[eNum.Key.ToString()].Tag.ToString();
									if (clsName != null && strAttribName != null)
									{
										dataType = Helper.DbInteraction.GetDatatype(clsName, strAttribName);
									}
								}
								if (dataType != null)
								{
									if (Helper.IsPrimitive(dataType))
									{
										if (eNum.Value != null)
										{
											this.Rows[listCount].Cells[eNum.Key.ToString()].Value = Helper.GetValue(dataType, eNum.Value);
											this.Rows[listCount].Cells[eNum.Key.ToString()].Tag = dataType;
											this.Rows[listCount].Cells[eNum.Key.ToString()].ReadOnly = false;

										}
										else
										{
											this.Rows[listCount].Cells[eNum.Key.ToString()].Value = VALUE_NULL;
											this.Rows[listCount].Cells[eNum.Key.ToString()].ReadOnly = true;
										}
									}
									else
									{
										if (eNum.Value != null)
										{
											this.Rows[listCount].Cells[eNum.Key.ToString()].Value = eNum.Value.ToString();
										}
										else
										{
											this.Rows[listCount].Cells[eNum.Key.ToString()].Value = VALUE_NULL;
										}
										this.Rows[listCount].Cells[eNum.Key.ToString()].ReadOnly = true;
									}
								}
							}
							else
								this.Rows[listCount].Tag = eNum.Value;
						}
					}
				}
				//this.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.AllCells;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Listing Helper Class

		#region Member variables

		#region Column Header Variables

		//QueryBuilder
		private string m_FieldHeaderText;
		private string m_ConditionHeaderText;
		private string m_ValueHeaderText;
		private string m_OperatorHeaderText;

		//Property Table for Class
		private string m_FieldNameHeaderText;
		private string m_DataTypeHeaderText;
		private string m_IsIndexedHeaderText;
		private string m_IsPublicHeaderText;

		//Class Property Table for Objects
		private string m_UUIDHeaderText;
		private string m_LocalIDHeaderText;
		private string m_ObjectDepthHeaderText;
		private string m_VersionHeaderText;

		private string m_AttributesHeaderText;

		//Database Property Table for Objects
		private string m_DataBaseSizeHeaderText;
		private string m_NumberOfClassesHeaderText;
		private string m_FreeSpaceHeaderText;

		#endregion

		#endregion

		#region Initialization Columns Methods

		/// <summary>
		/// Initializing Class Properties columns
		/// </summary>
		internal void InitClassPropertyColumns()
		{
			try
			{
				//Set header text for the Properties grid columns
				SetLiteralsClassPropertyColumn();

				//Clear all the columns before creating columns
				this.Columns.Clear();

				this.AutoGenerateColumns = false;
				this.AllowUserToDeleteRows = false;
				//  this.ReadOnly = true;

				DataGridViewTextBoxColumn fieldNameTextBoxColumn =
					CreateTextBoxColumn(m_FieldNameHeaderText, m_FieldNameHeaderText, DataGridViewColumnSortMode.NotSortable);
				fieldNameTextBoxColumn.ReadOnly = true;
				DataGridViewTextBoxColumn dataTypeTextBoxColumn =
					 CreateTextBoxColumn(m_DataTypeHeaderText, m_DataTypeHeaderText, DataGridViewColumnSortMode.NotSortable);
				dataTypeTextBoxColumn.ReadOnly = true;

				DataGridViewCheckBoxColumn isIndexedCheckBoxColumn =
					 CreateCheckBoxColumn(m_IsIndexedHeaderText, m_IsIndexedHeaderText, DataGridViewColumnSortMode.NotSortable);
				isIndexedCheckBoxColumn.ReadOnly = false;


				DataGridViewCheckBoxColumn isPublicCheckBoxColumn =
					CreateCheckBoxColumn(m_IsPublicHeaderText, m_IsPublicHeaderText, DataGridViewColumnSortMode.NotSortable);
				isPublicCheckBoxColumn.ReadOnly = true;

				this.Columns.AddRange(
					new DataGridViewColumn[] {
                    fieldNameTextBoxColumn, 
                    dataTypeTextBoxColumn,
                    isIndexedCheckBoxColumn,
                    isPublicCheckBoxColumn
                });

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Initializing Class QueryBuilder columns
		/// </summary>
		internal void InitQueryBuilderColumns()
		{
			try
			{
				//Set header text for the Query grid columns
				SetLiteralsQueryGridColumn();

				//Clear all the columns before creating columns
				this.Columns.Clear();

				this.AutoGenerateColumns = false;
				this.AllowUserToAddRows = false;
				this.AllowUserToDeleteRows = false;

				DataGridViewTextBoxColumn fieldNameTextBoxColumn =
					CreateTextBoxColumn(m_FieldHeaderText, m_FieldHeaderText, DataGridViewColumnSortMode.NotSortable);
				fieldNameTextBoxColumn.ReadOnly = true;

				DataGridViewComboBoxColumn conditionComboBoxColumn =
					CreateComboBoxColumn(m_ConditionHeaderText, m_ConditionHeaderText, DataGridViewColumnSortMode.NotSortable);
				dbDataGridViewDateTimePickerColumn valueTextBoxColumn =
					CreateDateTimeAndComboBoxColumn(m_ValueHeaderText, m_ValueHeaderText, DataGridViewColumnSortMode.NotSortable);
				DataGridViewComboBoxColumn operatorComboBoxColumn =
					 CreateComboBoxColumn(m_OperatorHeaderText, m_OperatorHeaderText, DataGridViewColumnSortMode.NotSortable);
				DataGridViewTextBoxColumn classNameTextBoxColumn =
					CreateTextBoxColumn(string.Empty, Constants.QUERY_GRID_CALSSNAME_HIDDEN, DataGridViewColumnSortMode.NotSortable);
				classNameTextBoxColumn.Visible = false;

				DataGridViewTextBoxColumn fieldTypeTextBoxColumn =
								   CreateTextBoxColumn(string.Empty, Constants.QUERY_GRID_FIELDTYPE_HIDDEN,
								   DataGridViewColumnSortMode.NotSortable);
				fieldTypeTextBoxColumn.Visible = false;

				fieldNameTextBoxColumn.Width = 230;
				conditionComboBoxColumn.Width = 100;
				valueTextBoxColumn.Width = 210;
				operatorComboBoxColumn.Width = 97;

				this.Columns.AddRange(
					new DataGridViewColumn[] {
                    fieldNameTextBoxColumn, 
                    conditionComboBoxColumn,
                    valueTextBoxColumn,
                    operatorComboBoxColumn,
                    classNameTextBoxColumn,
                    fieldTypeTextBoxColumn
                });

				//Prepare Oprator Column
				FillOperatorComboBox();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Initializing Class Attributes columns
		/// </summary>
		internal void InitAttributeColumns()
		{
			try
			{
				//Set header text for the attribute grid column
				SetLitralsAdttributesColumn();

				//Clear all the columns before creating columns
				this.Columns.Clear();

				this.AutoGenerateColumns = false;
				//this.AllowUserToAddRows = false;
				this.AllowUserToDeleteRows = true;
				this.ReadOnly = true;

				DataGridViewTextBoxColumn fieldNameTextBoxColumn =
					CreateTextBoxColumn(m_AttributesHeaderText, m_AttributesHeaderText, DataGridViewColumnSortMode.NotSortable);

				this.Columns.Add(fieldNameTextBoxColumn);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Initializing Database Property columns
		/// </summary>
		internal void InitDatabasePropertyColumns()
		{
			try
			{
				//Set header text for the Database grid column
				SetLitralsDatabasePropertiesColumn();

				//Clear all the columns before creating columns
				this.Columns.Clear();

				this.AutoGenerateColumns = false;
				this.AllowUserToDeleteRows = false;
				this.ReadOnly = true;

				DataGridViewTextBoxColumn databaseSizeTextBoxColumn =
					CreateTextBoxColumn(m_DataBaseSizeHeaderText, m_DataBaseSizeHeaderText,
										DataGridViewColumnSortMode.NotSortable);

				DataGridViewTextBoxColumn noOfClasessTextBoxColumn =
					CreateTextBoxColumn(m_NumberOfClassesHeaderText, m_NumberOfClassesHeaderText,
										DataGridViewColumnSortMode.NotSortable);

				DataGridViewTextBoxColumn freeSpaceTextBoxColumn =
				   CreateTextBoxColumn(m_FreeSpaceHeaderText, m_FreeSpaceHeaderText,
									   DataGridViewColumnSortMode.NotSortable);

				this.Columns.AddRange(
				   new DataGridViewColumn[] {
                    databaseSizeTextBoxColumn, 
                    noOfClasessTextBoxColumn,
                    freeSpaceTextBoxColumn
                });
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Initializing Objects Property columns
		/// </summary>
		internal void InitObjectsPropertyColumns()
		{
			try
			{
				//Set header text for the Object grid column
				SetLitralsObjectProperties();

				//Clear all the columns before creating columns
				this.Columns.Clear();

				this.AutoGenerateColumns = false;
				this.AllowUserToDeleteRows = false;
				this.ReadOnly = true;

				DataGridViewTextBoxColumn uuidTextBoxColumn =
					CreateTextBoxColumn(m_UUIDHeaderText, m_UUIDHeaderText, DataGridViewColumnSortMode.NotSortable);

				DataGridViewTextBoxColumn localIDTextBoxColumn =
					CreateTextBoxColumn(m_LocalIDHeaderText, m_LocalIDHeaderText, DataGridViewColumnSortMode.NotSortable);

				DataGridViewTextBoxColumn versionTextBoxColumn =
					CreateTextBoxColumn(m_VersionHeaderText, m_VersionHeaderText, DataGridViewColumnSortMode.NotSortable);

				this.Columns.AddRange(
				   new DataGridViewColumn[] {
                    uuidTextBoxColumn, 
                    localIDTextBoxColumn,
                    versionTextBoxColumn
                });
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// Set litrals for class properties
		/// </summary>
		private void SetLiteralsClassPropertyColumn()
		{
			try
			{
				m_FieldNameHeaderText = Helper.GetResourceString(Constants.CLASS_PROPERTY_FIELD_NAME);
				m_DataTypeHeaderText = Helper.GetResourceString(Constants.CLASS_PROPERTY_DATA_TYPE);
				m_IsIndexedHeaderText = Helper.GetResourceString(Constants.CLASS_PROPERTY_ISINDEXED);
				m_IsPublicHeaderText = Helper.GetResourceString(Constants.CLASS_PROPERTY_ISPUBLIC);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		private void SetLiteralsQueryGridColumn()
		{
			try
			{
				m_FieldHeaderText = Helper.GetResourceString(Constants.QUERY_GRID_FIELD);
				m_ConditionHeaderText = Helper.GetResourceString(Constants.QUERY_GRID_CONDITION);
				m_ValueHeaderText = Helper.GetResourceString(Constants.QUERY_GRID_VALUE);
				m_OperatorHeaderText = Helper.GetResourceString(Constants.QUERY_GRID_OPERATOR);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void SetLitralsAdttributesColumn()
		{
			try
			{
				m_AttributesHeaderText = Helper.GetResourceString(Constants.ATTRIB_TEXT);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void SetLitralsDatabasePropertiesColumn()
		{
			try
			{
				m_DataBaseSizeHeaderText = Helper.GetResourceString(Constants.DB_PROPERTY_SIZE);
				m_NumberOfClassesHeaderText = Helper.GetResourceString(Constants.DB_PROPERTY_CLASSES);
				m_FreeSpaceHeaderText = Helper.GetResourceString(Constants.DB_PROPERTY_FREESPACE);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void SetLitralsObjectProperties()
		{
			try
			{
				m_UUIDHeaderText = Helper.GetResourceString(Constants.OBJECT_PROPERTY_UUID);
				m_LocalIDHeaderText = Helper.GetResourceString(Constants.OBJECT_PROPERTY_LOCALID);
				m_ObjectDepthHeaderText = Helper.GetResourceString(Constants.OBJECT_PROPERTY_DEPTH);
				m_VersionHeaderText = Helper.GetResourceString(Constants.OBJECT_PROPERTY_VERSION);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		/// <summary>
		/// Create a new data grid view column of type DataGridViewTextboxColumn
		/// </summary>
		/// <param name="headertext"></param>
		/// <param name="name"></param>
		/// <param name="sortMode"></param>
		/// <returns></returns>
		internal DataGridViewTextBoxColumn CreateTextBoxColumn
			(
				string headertext,
				string name,
				DataGridViewColumnSortMode sortMode)
		{

			DataGridViewTextBoxColumn newDataGridViewTextBoxColumn;
			newDataGridViewTextBoxColumn = new DataGridViewTextBoxColumn();
			newDataGridViewTextBoxColumn.HeaderText = headertext;
			newDataGridViewTextBoxColumn.AutoSizeMode =
				DataGridViewAutoSizeColumnMode.None;
			newDataGridViewTextBoxColumn.SortMode = sortMode;
			newDataGridViewTextBoxColumn.Name = name;
			return newDataGridViewTextBoxColumn;
		}

		/// <summary>
		/// Create a new data grid view column of type AIDataGridViewImageComboxColumn
		/// </summary>
		/// <param name="headertext"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		internal DataGridViewComboBoxColumn CreateComboBoxColumn(
			string headertext, string name, DataGridViewColumnSortMode sortMode)
		{

			DataGridViewComboBoxColumn newDataGridViewComboBoxColumn;
			newDataGridViewComboBoxColumn = new DataGridViewComboBoxColumn();
			newDataGridViewComboBoxColumn.HeaderText = headertext;
			newDataGridViewComboBoxColumn.AutoSizeMode =
				DataGridViewAutoSizeColumnMode.None;
			newDataGridViewComboBoxColumn.SortMode = sortMode;
			newDataGridViewComboBoxColumn.Name = name;
			newDataGridViewComboBoxColumn.Resizable = DataGridViewTriState.True;
			newDataGridViewComboBoxColumn.AutoSizeMode = DataGridViewAutoSizeColumnMode.None;
			newDataGridViewComboBoxColumn.FlatStyle = FlatStyle.Popup;
			return newDataGridViewComboBoxColumn;
		}


		/// <summary>
		/// Create a new data grid view column of type AIDataGridViewImageComboxColumn
		/// </summary>
		/// <param name="headertext"></param>
		/// <param name="name"></param>
		/// <returns></returns>
		internal DataGridViewCheckBoxColumn CreateCheckBoxColumn(
			string headertext, string name, DataGridViewColumnSortMode sortMode)
		{

			DataGridViewCheckBoxColumn newDataGridViewCheckBoxColumn;
			newDataGridViewCheckBoxColumn = new DataGridViewCheckBoxColumn();
			newDataGridViewCheckBoxColumn.HeaderText = headertext;
			newDataGridViewCheckBoxColumn.AutoSizeMode =
				DataGridViewAutoSizeColumnMode.None;
			newDataGridViewCheckBoxColumn.SortMode = sortMode;
			newDataGridViewCheckBoxColumn.Name = name;
			return newDataGridViewCheckBoxColumn;
		}


		/// <summary>
		/// Create a new data grid view column of type DataGridViewTextboxColumn
		/// </summary>
		/// <param name="headertext"></param>
		/// <param name="name"></param>
		/// <param name="sortMode"></param>
		/// <returns></returns>
		internal DataGridViewImageColumn CreateImageColumn
			(
				string headertext,
				string name,
				DataGridViewColumnSortMode sortMode)
		{

			DataGridViewImageColumn newDataGridViewImageColumn;
			newDataGridViewImageColumn = new DataGridViewImageColumn();
			newDataGridViewImageColumn.HeaderText = headertext;
			newDataGridViewImageColumn.AutoSizeMode =
				DataGridViewAutoSizeColumnMode.None;
			newDataGridViewImageColumn.Resizable = DataGridViewTriState.False;
			newDataGridViewImageColumn.SortMode = sortMode;
			newDataGridViewImageColumn.Name = name;
			newDataGridViewImageColumn.Image = dbImages.Delete;
			newDataGridViewImageColumn.Width = dbImages.Delete.Width + 5;
			return newDataGridViewImageColumn;
		}

		/// <summary>
		/// Create a new data grid view column of type DataGridViewTextboxColumn
		/// </summary>
		/// <param name="headertext"></param>
		/// <param name="name"></param>
		/// <param name="sortMode"></param>
		/// <returns></returns>
		internal dbDataGridViewDateTimePickerColumn CreateDateTimeAndComboBoxColumn
			(
				string headertext,
				string name,
				DataGridViewColumnSortMode sortMode)
		{

			dbDataGridViewDateTimePickerColumn newDataGridViewTextBoxColumn;
			newDataGridViewTextBoxColumn = new dbDataGridViewDateTimePickerColumn();
			newDataGridViewTextBoxColumn.HeaderText = headertext;
			newDataGridViewTextBoxColumn.AutoSizeMode =
				DataGridViewAutoSizeColumnMode.None;
			newDataGridViewTextBoxColumn.SortMode = sortMode;
			newDataGridViewTextBoxColumn.Name = name;
			return newDataGridViewTextBoxColumn;
		}
		#endregion

		#region Public Method

		internal void PopulateDisplayGrid(string viewName, ArrayList dataSource)
		{

			try
			{
				if (this.Columns.Count < 1)
				{
					switch (viewName)
					{
						case Constants.VIEW_CLASSPOPERTY:
							InitClassPropertyColumns();
							break;
						case Constants.VIEW_QUERYBUILDER:
							InitQueryBuilderColumns();
							break;
						case Constants.VIEW_ATTRIBUTES:
							InitAttributeColumns();
							break;
						case Constants.VIEW_DBPROPERTIES:
							InitDatabasePropertyColumns();
							break;
						case Constants.VIEW_OBJECTPROPERTIES:
							InitObjectsPropertyColumns();
							break;
						default:
							break;
					}
				}

				if (dataSource != null)
				{
					this.DataSource = null;

					if (!this.AutoGenerateColumns)
						SetData(viewName, dataSource);
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		/// <summary>
		/// SetData : It is used to bind data to datagrid view (e.g. simple listing for souces,services etc.)
		/// </summary>

		/// <param name="data">ArrayList : arryaList of entity object</param>
		/// <param name="name">string: entity type, it is used to get corresponding image</param>
		private void SetData(string name, ArrayList data)
		{
			try
			{
				int colCount = this.Columns.Count;
				if (colCount > 0)
				{
					//HideGridColumns(dataGridView);

					if (data != null)
					{
						object dataobject;
						string colname;
						int dataCount = data.Count;
						//int orderColumnStartValue = Controller.GetPageSize *
						//    (Controller.CurrentPageNumber - 1);
						for (int dataIndex = 0; dataIndex < dataCount; dataIndex++)
						{
							dataobject = data[dataIndex];
							this.Rows.Add(new DataGridViewRow());

							for (int colIndex = 0; colIndex < colCount; colIndex++)
							{
								DataGridViewColumn col = this.Columns[colIndex];
								colname = col.Name;

								System.Type coltype = col.GetType();
								System.Type dataobjectType = dataobject.GetType();
								PropertyInfo pi = dataobjectType.GetProperty(colname);
								if (pi != null)
								{
									FillDataIntoDataGridCell(name,
										dataobject,
										colname,
										dataIndex,
										coltype,
										pi);
								}
							}

							//Adjust the height of specified row
							if (this.Rows[dataIndex] != null &&
								dataIndex != -1)
								this.AutoResizeRow(dataIndex);
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void FillDataIntoDataGridCell(string name,
			object dataobject,
			string colname,
			int dataIndex,
			System.Type coltype,
			PropertyInfo pi)
		{
			try
			{
				string coltypename = coltype.ToString();
				object dataObjectValue = pi.GetValue(dataobject, null);

				//Check for the object type.
				if (dataObjectValue != null)
				{
					string val = string.Empty;
					switch (coltypename)
					{
						//case Constants.DBDATAGRIDVIEW_TEXTBOX_COLUMN:
						//     break;
						case Constants.DBDATAGRIDVIEW_COMBOBOX_COLUMN:
							#region Image Combobox
							DataGridViewComboBoxCell dataGridViewComboBoxCell =
								this.Rows[dataIndex].Cells[colname] as DataGridViewComboBoxCell;
							#endregion Image Combobox
							break;
						case Constants.DBDATAGRIDVIEW_CHECKBOX_COLUMN:
							#region Checkbox column
							val = string.Empty;
							if (dataObjectValue != null)
								val = dataObjectValue.ToString();

							this.Rows[dataIndex].Cells[colname].Value = val;
							#endregion Checkbox column
							break;
						default:
							#region Text without image
							//only text to be displayed in the cell
							val = string.Empty;
							if (dataObjectValue != null)
							{
								val = dataObjectValue.ToString();
							}
							this.Rows[dataIndex].Cells[colname].Style.WrapMode =
							   DataGridViewTriState.True;
							//Adjust the height of specified row
							this.AutoResizeRow(dataIndex);
							this.Rows[dataIndex].Cells[colname].Value = val;
							#endregion Text without image
							break;
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Query Helper Methods

		internal void FillConditionsCombobox(string typeofnode, int selectedrowindex)
		{
			DataGridViewComboBoxCell conditionColumn = null;
			string conditionColumnName = string.Empty;
			string[] conditionList = null;

			try
			{
				QueryHelper qHelper = new QueryHelper(typeofnode);
				conditionList = qHelper.GetConditions();
				conditionColumnName = Helper.GetResourceString(Constants.QUERY_GRID_CONDITION);
				conditionColumn = (DataGridViewComboBoxCell)this.Rows[selectedrowindex].Cells[conditionColumnName];
				conditionColumn.OwningColumn.MinimumWidth = 40;
				conditionColumn.MaxDropDownItems = conditionList.Length;
				conditionColumn.Items.Clear();
				conditionColumn.Items.AddRange(conditionList);
				this.Rows[selectedrowindex].Cells[conditionColumnName].Value = conditionList.GetValue(0);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public void FillOperatorComboBox()
		{
			DataGridViewComboBoxColumn operatorColumn = null;
			string operatorColumnName = string.Empty;
			string[] opratorList = null;

			try
			{
				opratorList = QueryHelper.GetOperators();

				operatorColumnName = Helper.GetResourceString(Constants.QUERY_GRID_OPERATOR);
				operatorColumn = (DataGridViewComboBoxColumn)this.Columns[operatorColumnName];
				operatorColumn.MaxDropDownItems = opratorList.Length;
				operatorColumn.Items.Clear();
				operatorColumn.Items.AddRange(opratorList);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}



		#endregion

		#endregion

		/// <summary>
		/// Adds the selected field to DataGridView while Dragged to QueryBuilder or by selecting the
		/// context menu "Add to Query" option.
		/// </summary>
		/// <param name="datagridObject"></param>
		/// <param name="tempTreeNode"></param>
		internal bool AddToQueryBuilder(TreeNode tempTreeNode, QueryBuilder queryBuilder)
		{
			string typeOfNode = string.Empty;
			string fullpath = string.Empty;
			string className = string.Empty;
			//QueryBuilder queryBuilder = new QueryBuilder(); 
			try
			{
				//Get the type of sselected item from tree view
				typeOfNode = Helper.GetTypeOfObject(tempTreeNode.Tag.ToString());

				//If selected item is not a primitive type than dont allow to drage item
				if (!Helper.IsPrimitive(typeOfNode))
					return false;

				if (tempTreeNode.Parent != null)
				{
					//Get the class name including the assembly information
					if (tempTreeNode.Parent.Tag.ToString().Contains(","))
						className = tempTreeNode.Parent.Tag.ToString();
					else
						className = tempTreeNode.Parent.Name;
				}

				//If field is not selected and Query Group has no clauses then reset the base class.
				if (queryBuilder.QueryGroupCount == 0 && this.Rows.Count == 0 && queryBuilder.AttributeCount == 0)
				{
					Helper.HashTableBaseClass.Clear();
				}

				//Get full path of selected node
				fullpath = Helper.GetFullPath(tempTreeNode);

				//Chech whether dragged item is from same class or not,
				//if not dont allow to drop that item in query builder
				if (Helper.HashTableBaseClass.Count > 0)
					if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
						return false;

				//add a new row to Query Grid
				AddRowsToGrid(typeOfNode, className, fullpath);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
				return false;
			}

			return Rows.Count > 0;
		}

		internal bool AddAllItemsOfClassToQueryBuilder(TreeNode tempTreeNode, QueryBuilder queryBuilder)
		{
			string typeOfNode = string.Empty;
			string fullpath = string.Empty;
			string className = string.Empty;
			//QueryBuilder queryBuilder = new QueryBuilder(); 
			try
			{
				//Get the type of sselected item from tree view

				Helper.BaseClass = Helper.FindRootNode(tempTreeNode);
				if (Helper.HashTableBaseClass.Count > 0)
					if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
						return false;

				if (tempTreeNode.Tag.ToString().Contains(","))
					className = tempTreeNode.Tag.ToString();
				else
					className = tempTreeNode.Name.ToString();

				Hashtable storedfields = Helper.DbInteraction.FetchStoredFields(className);
				if (storedfields != null)
				{
					IDictionaryEnumerator eNum = storedfields.GetEnumerator();
					if (eNum != null)
					{
						while (eNum.MoveNext())
						{
							typeOfNode = Helper.GetTypeOfObject(eNum.Value.ToString());
							if (!Helper.IsPrimitive(typeOfNode))
								continue;
							else
							{
								if (queryBuilder.QueryGroupCount == 0 && this.Rows.Count == 0 && queryBuilder.AttributeCount == 0)
								{
									Helper.HashTableBaseClass.Clear();
								}

								//Chech whether dragged item is from same class or not,
								//if not dont allow to drop that item in query builder
								string parentName = Helper.FormulateParentName(tempTreeNode, eNum);
								AddRowsToGrid(typeOfNode, className, parentName);
							}

						}
					}

				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
				return false;
			}

			return Rows.Count > 0;
		}





		private void AddRowsToGrid(string typeOfNode, string className, string parentName)
		{
			try
			{
				this.Rows.Add(1);
				DataGridViewComboBoxColumn conditionColumn = (DataGridViewComboBoxColumn)this.Columns[1];
				int index = this.Rows.Count - 1;
				this.Rows[index].Cells[0].Value = parentName;
				this.Rows[index].Cells[Constants.QUERY_GRID_CALSSNAME_HIDDEN].Value = className;
				this.Rows[index].Cells[Constants.QUERY_GRID_FIELDTYPE_HIDDEN].Value = typeOfNode;
				if (typeOfNode == BusinessConstants.BOOLEAN)
				{
					this.Rows[index].Cells[2].Value = "True";
				}
				if (typeOfNode == BusinessConstants.DATETIME)
				{
					this.Rows[index].Cells[2].Value = DateTime.Now.ToString();
				}
				this.ClearSelection();
				this.Rows[index].Cells[0].Selected = true;


				if (!Helper.HashTableBaseClass.Contains(Helper.BaseClass))
					Helper.HashTableBaseClass.Add(Helper.BaseClass, string.Empty);

				//Fill the Conditions depending upon the field name
				this.FillConditionsCombobox(typeOfNode, index);

				//Select the Default operator for Query
				this.Rows[index].Cells[3].Value = CommonValues.LogicalOperators.AND.ToString();

				//OM MISHRA: disable operator cell for other than fist row 
				if (index > 0)
				{
					this.Rows[index].Cells[3].Value = this.Rows[0].Cells[3].Value;
					this.Rows[index].Cells[3].ReadOnly = true;
				}
				else
				{
					//Select the Default operator for Query
					this.Rows[index].Cells[3].Value = CommonValues.LogicalOperators.AND.ToString();
				}
			}

			catch (Exception ex)
			{

				LoggingHelper.ShowMessage(ex);

			}

		}
		/// <summary>
		/// dbDataGridViewEventArgs : For handling all  custom events of AIDataGridView
		/// </summary>
		/// 

	}
	public class dbDataGridViewEventArgs : System.EventArgs
	{
		private object m_data = string.Empty;

		public object Data
		{
			get { return m_data; }
		}

		public dbDataGridViewEventArgs(object data)
		{
			m_data = data;
		}

		public dbDataGridViewEventArgs()
		{
		}
	}
}

