namespace Db4objects.Db4o.Types
{
	/// <summary>
	/// the db4o Blob type to store blobs independant of the main database
	/// file and allows to perform asynchronous upload and download operations.
	/// </summary>
	/// <remarks>
	/// the db4o Blob type to store blobs independant of the main database
	/// file and allows to perform asynchronous upload and download operations.
	/// <br /><br />
	/// <b>Usage:</b><br />
	/// - Define Blob fields on your user classes.<br />
	/// - As soon as an object of your class is stored, db4o automatically
	/// takes care that the Blob field is set.<br />
	/// - Call readFrom to read a blob file into the db4o system.<br />
	/// - Call writeTo to write a blob file from within the db4o system.<br />
	/// - getStatus may help you to determine, whether data has been
	/// previously stored. It may also help you to track the completion
	/// of the current process.
	/// <br /><br />
	/// db4o client/server carries out all blob operations in a seperate
	/// thread on a specially dedicated socket. One socket is used for
	/// all blob operations and operations are queued. Your application
	/// may continue to access db4o while a blob is transferred in the
	/// background.
	/// </remarks>
	public interface IBlob : Db4objects.Db4o.Types.IDb4oType
	{
		/// <summary>returns the name of the file the blob was stored to.</summary>
		/// <remarks>
		/// returns the name of the file the blob was stored to.
		/// <br /><br />The method may return null, if the file was never
		/// stored.
		/// </remarks>
		/// <returns>String the name of the file.</returns>
		string GetFileName();

		/// <summary>returns the status after the last read- or write-operation.</summary>
		/// <remarks>
		/// returns the status after the last read- or write-operation.
		/// <br /><br />The status value returned may be any of the following:<br />
		/// Status.UNUSED  no data was ever stored to the Blob field.<br />
		/// Status.AVAILABLE available data was previously stored to the Blob field.<br />
		/// Status.QUEUED an operation was triggered and is waiting for it's turn in the Blob queue.<br />
		/// Status.COMPLETED the last operation on this field was completed successfully.<br />
		/// Status.PROCESSING for internal use only.<br />
		/// Status.ERROR the last operation failed.<br />
		/// or a double between 0 and 1 that signifies the current completion percentage of the currently
		/// running operation.<br /><br /> the five STATUS constants defined in this interface or a double
		/// between 0 and 1 that signifies the completion of the currently running operation.<br /><br />
		/// </remarks>
		/// <returns>status - the current status</returns>
		/// <seealso cref="Db4objects.Db4o.Ext.Status">STATUS constants</seealso>
		double GetStatus();

		/// <summary>reads a file into the db4o system and stores it as a blob.</summary>
		/// <remarks>
		/// reads a file into the db4o system and stores it as a blob.
		/// <br /><br />
		/// In Client/Server mode db4o will open an additional socket and
		/// process writing data in an additional thread.
		/// <br /><br />
		/// </remarks>
		/// <param name="file">the file the blob is to be read from.</param>
		/// <exception cref="System.IO.IOException">in case of errors</exception>
		void ReadFrom(Sharpen.IO.File file);

		/// <summary>reads a file into the db4o system and stores it as a blob.</summary>
		/// <remarks>
		/// reads a file into the db4o system and stores it as a blob.
		/// <br /><br />
		/// db4o will use the local file system in Client/Server mode also.
		/// <br /><br />
		/// </remarks>
		/// <param name="file">the file the blob is to be read from.</param>
		/// <exception cref="System.IO.IOException">in case of errors</exception>
		void ReadLocal(Sharpen.IO.File file);

		/// <summary>writes stored blob data to a file.</summary>
		/// <remarks>
		/// writes stored blob data to a file.
		/// <br /><br />
		/// db4o will use the local file system in Client/Server mode also.
		/// <br /><br />
		/// </remarks>
		/// <exception cref="System.IO.IOException">
		/// in case of errors and in case no blob
		/// data was stored
		/// </exception>
		/// <param name="file">the file the blob is to be written to.</param>
		void WriteLocal(Sharpen.IO.File file);

		/// <summary>writes stored blob data to a file.</summary>
		/// <remarks>
		/// writes stored blob data to a file.
		/// <br /><br />
		/// In Client/Server mode db4o will open an additional socket and
		/// process writing data in an additional thread.
		/// <br /><br />
		/// </remarks>
		/// <exception cref="System.IO.IOException">
		/// in case of errors and in case no blob
		/// data was stored
		/// </exception>
		/// <param name="file">the file the blob is to be written to.</param>
		void WriteTo(Sharpen.IO.File file);
	}
}
